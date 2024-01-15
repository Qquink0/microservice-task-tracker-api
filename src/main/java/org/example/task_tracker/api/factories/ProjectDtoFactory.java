package org.example.task_tracker.api.factories;

import org.example.task_tracker.api.dto.ProjectDTO;
import org.example.task_tracker.store.entities.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoFactory {

    public ProjectDTO makeProjectDto(ProjectEntity entity) {
        return ProjectDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
