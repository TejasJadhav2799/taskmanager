package com.thinkalike.taskmanager.kafka;

import com.thinkalike.taskmanager.event.TaskAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public static final String TASK_ASSIGNED_TOPIC = "task-assigned";

    public void publishTaskAssigned(TaskAssignedEvent event) {
        try {
            // manually build JSON string — avoids Jackson version conflict
            // in production you'd use a proper serializer configured on KafkaTemplate
            String message = String.format(
                    "{\"taskId\":%d,\"taskTitle\":\"%s\",\"assigneeId\":%d," +
                            "\"assigneeName\":\"%s\",\"assigneeEmail\":\"%s\"," +
                            "\"projectId\":%d,\"projectName\":\"%s\"}",
                    event.getTaskId(),
                    event.getTaskTitle(),
                    event.getAssigneeId(),
                    event.getAssigneeName(),
                    event.getAssigneeEmail(),
                    event.getProjectId(),
                    event.getProjectName()
            );

            kafkaTemplate.send(TASK_ASSIGNED_TOPIC,
                    event.getTaskId().toString(), message);

            log.info("Published task-assigned event for task: {} assigned to: {}",
                    event.getTaskTitle(), event.getAssigneeName());

        } catch (Exception e) {
            log.error("Failed to publish task-assigned event: {}", e.getMessage());
        }
    }
}