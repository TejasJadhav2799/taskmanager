package com.thinkalike.taskmanager.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignedEvent {
    // this is the message we publish to Kafka
    // it contains everything the consumer needs to know
    private Long taskId;
    private String taskTitle;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeEmail;
    private Long projectId;
    private String projectName;
}