package org.example.task_tracker.api.factories;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.task_tracker.api.dto.TaskStateDTO;
import org.example.task_tracker.store.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class TaskStateDtoFactory {

    TaskDtoFactory taskDtoFactory;

    public TaskStateDTO makeTaskStateDto(TaskStateEntity entity) {
        return TaskStateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .ordinal(entity.getOrdinal())
                .tasks(entity.getTasks()
                        .stream()
                        .map(taskDtoFactory::makeTaskDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
