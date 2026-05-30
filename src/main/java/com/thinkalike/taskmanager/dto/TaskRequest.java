package com.thinkalike.taskmanager.dto;

import com.thinkalike.taskmanager.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private Task.TaskStatus status;

    private Task.Priority priority;

    private LocalDate dueDate;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    // assignee is optional — task may not be assigned initially
    private Long assigneeId;

    @NotNull(message = "Creator ID is required")
    private Long createdById;
}
