package org.example.task_tracker.api.factories;

import org.example.task_tracker.api.dto.TaskStateDTO;
import org.example.task_tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskStateDtoFactory {

    public TaskStateDTO makeTaskStateDto(TaskStateEntity entity) {
        return TaskStateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .ordinal(entity.getOrdinal())
                .build();
    }
}
