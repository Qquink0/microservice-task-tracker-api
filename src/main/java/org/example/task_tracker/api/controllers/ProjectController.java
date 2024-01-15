package org.example.task_tracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task_tracker.api.dto.AskDTO;
import org.example.task_tracker.api.dto.ProjectDTO;
import org.example.task_tracker.api.exception.BadRequestException;
import org.example.task_tracker.api.exception.NotFoundException;
import org.example.task_tracker.api.factories.ProjectDtoFactory;
import org.example.task_tracker.store.entities.ProjectEntity;
import org.example.task_tracker.store.repositories.ProjectRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //non stable
@Transactional //bad practice
@RestController
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    public static final String FETCH_PROJECT = "/api/projects";
    //    public static final String CREATE_PROJECT = "/api/projects";
//    public static final String EDIT_PROJECT = "/api/projects/{project_id}";
    public static final String DELETE_PROJECT = "/api/projects/{project_id}";

    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";

    @GetMapping(FETCH_PROJECT)
    public List<ProjectDTO> fetchProject(
            @RequestParam(value = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAll);

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

//    @PostMapping(CREATE_PROJECT)
//    public ProjectDTO createProject(@RequestParam("project_name") String projectName) {
//
//        if (projectName.trim().isEmpty()) {
//            throw new BadRequestException("Name can't be empty");
//        }
//
//        if (projectRepository.findByName(projectName).isPresent()) {
//            throw new BadRequestException(String.format("Project \"%s\" already exist.", projectName));
//        }
//
//        ProjectEntity project = projectRepository.saveAndFlush(
//                ProjectEntity.builder()
//                        .name(projectName)
//                        .build()
//        );
//
//        return projectDtoFactory.makeProjectDto(project);
//    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDTO createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName
            // Another params...
    ) {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = optionalProjectId.isEmpty();

        if (isCreate && optionalProjectName.isEmpty()) {
            throw new BadRequestException("Project name can't be empty.");
        }

        final ProjectEntity project = optionalProjectId
                .map(this::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());


        optionalProjectName
                .ifPresent(projectName -> {

                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                            .ifPresent(anotherProject -> {
                                throw new BadRequestException(
                                        String.format("Project \"%s\" already exist.", projectName));
                            });

                    project.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);


        return projectDtoFactory.makeProjectDto(savedProject);
    }

//    @PatchMapping(EDIT_PROJECT)
//    public ProjectDTO editProject(@PathVariable("project_id") Long projectId,
//                                  @RequestParam("project_name") String projectName) {
//
//        if (projectName.trim().isEmpty()) {
//            throw new BadRequestException("Name can't be empty");
//        }
//
//        ProjectEntity project = getProjectOrThrowException(projectId);
//
//        projectRepository
//                .findByName(projectName)
//                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
//                .ifPresent(anotherProject -> {
//                    throw new BadRequestException(String.format("Project \"%s\" already exist.", projectName));
//                });
//
//        project.setName(projectName);
//
//        projectRepository.saveAndFlush(project);
//
//        return projectDtoFactory.makeProjectDto(project);
//    }

    @DeleteMapping(DELETE_PROJECT)
    public AskDTO deleteProject(@PathVariable("project_id") Long projectId) {

        getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AskDTO.makeDefault(true);
    }

    private ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "Project with \"%s\" doesn't exist",
                                projectId)));
    }
}
