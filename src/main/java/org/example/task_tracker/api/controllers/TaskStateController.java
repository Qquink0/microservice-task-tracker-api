package org.example.task_tracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task_tracker.api.controllers.helpers.ControllerHelper;
import org.example.task_tracker.api.dto.TaskStateDTO;
import org.example.task_tracker.api.exception.BadRequestException;
import org.example.task_tracker.api.factories.TaskStateDtoFactory;
import org.example.task_tracker.store.entities.ProjectEntity;
import org.example.task_tracker.store.entities.TaskStateEntity;
import org.example.task_tracker.store.repositories.TaskStateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //non stable
@Transactional //bad practice
@RestController
public class TaskStateController {


    TaskStateRepository taskStateRepository;

    TaskStateDtoFactory taskStateDtoFactory;

    ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/api/projects{project_id}/tasks-states";
    public static final String CREATE_TASK_STATE = "/api/projects{project_id}/task-states";

    public static final String DELETE_PROJECT = "/api/projects/{project_id}";


    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDTO> getTaskStates(@PathVariable(name = "project_id") Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project.getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());

    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDTO createTaskState(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(name = "task_state_name") String taskStateName) {

        if (taskStateName.isBlank()) {
            throw new BadRequestException("Task state name can't be empty");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        project.getTaskStates().stream()
                .map(TaskStateEntity::getName)
                .filter(anotherTaskStateName -> anotherTaskStateName.equalsIgnoreCase(taskStateName))
                .findAny()
                .ifPresent(it -> {
                    throw new BadRequestException(String.format("Task state \"%s\" already exists", taskStateName));
                });

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder()
                        .name(taskStateName)
                        .build()
        );

        taskStateRepository
                .findTaskStateEntityByRightTaskStateIdIsNullAndProjectId(projectId)
                .ifPresent(anotherTaskState -> {
                    taskState.setLeftTaskState(anotherTaskState);
                    anotherTaskState.setRightTaskState(taskState);
                    taskStateRepository.saveAndFlush(anotherTaskState);
                });

        final TaskStateEntity savedTaskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.makeTaskStateDto(taskState);
    }
}
