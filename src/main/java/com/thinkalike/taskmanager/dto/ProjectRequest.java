package com.thinkalike.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    // owner is identified by their ID
    // the service will look up the actual User object from the database
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
