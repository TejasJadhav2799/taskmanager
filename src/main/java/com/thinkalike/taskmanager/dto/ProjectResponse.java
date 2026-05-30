package com.thinkalike.taskmanager.dto;

import com.thinkalike.taskmanager.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Project.ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // owner info — we embed just enough user info
    // not the full UserResponse — just id and name
    private Long ownerId;
    private String ownerName;

    public static ProjectResponse from (Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .ownerId(project.getOwner().getId())
                .ownerName(project.getOwner().getName())
                .build();
    }


}
