package org.example.task_tracker.store.repositories;

import org.example.task_tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStateRepository extends JpaRepository<TaskStateEntity, Long> {

    Optional<TaskStateEntity> findTaskStateEntitiesByProjectIdAndNameContainsIgnoreCase(Long projectId, String taskStateName);

}
