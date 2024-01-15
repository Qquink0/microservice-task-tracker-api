package org.example.task_tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskStateDTO {

    @NonNull
    Long id;

    @NonNull
    Long ordinal;

    @NonNull
    String name;

    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;
}
