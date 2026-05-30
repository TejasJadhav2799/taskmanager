package com.thinkalike.taskmanager.dto;

import com.thinkalike.taskmanager.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // project info
    private Long projectId;
    private String projectName;

    // assignee info — nullable because task may be unassigned
    private Long assigneeId;
    private String assigneeName;

    // creator info
    private Long createdById;
    private String createdByName;

    public static TaskResponse from(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                // assignee can be null so check before accessing
                .assigneeId(task.getAssignee() != null
                        ? task.getAssignee().getId() : null)
                .assigneeName(task.getAssignee() != null
                        ? task.getAssignee().getName() : null)
                .createdById(task.getCreatedBy().getId())
                .createdByName(task.getCreatedBy().getName())
                .build();
    }
}
