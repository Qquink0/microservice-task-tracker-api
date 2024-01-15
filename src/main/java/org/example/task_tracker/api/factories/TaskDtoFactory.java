package org.example.task_tracker.api.factories;

import org.example.task_tracker.api.dto.TaskDTO;
import org.example.task_tracker.store.entities.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDTO makeTaskDto(TaskEntity entity) {
        return TaskDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
