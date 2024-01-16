package org.example.task_tracker.api.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task_tracker.api.controllers.helpers.ControllerHelper;
import org.example.task_tracker.api.dto.TaskStateDTO;
import org.example.task_tracker.api.factories.TaskStateDtoFactory;
import org.example.task_tracker.store.entities.ProjectEntity;
import org.example.task_tracker.store.repositories.TaskStateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //non stable
@Transactional //bad practice
@RestController
public class TaskStateController {


    TaskStateRepository taskStateRepository;

    TaskStateDtoFactory taskStateDtoFactory;

    ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/api/projects{project_id}/tasks-states";

    public static final String DELETE_PROJECT = "/api/projects/{project_id}";

    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";

    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDTO> getTaskStates(@PathVariable(name = "project_id") Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project.getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());

    }
}
