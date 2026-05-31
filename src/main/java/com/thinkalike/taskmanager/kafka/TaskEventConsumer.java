package com.thinkalike.taskmanager.kafka;

import com.thinkalike.taskmanager.event.TaskAssignedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskEventConsumer {

    @KafkaListener(
            topics = TaskEventProducer.TASK_ASSIGNED_TOPIC,
            groupId = "taskmanager-group"
    )
    public void handleTaskAssigned(String message) {
        try {
            // parse the JSON fields manually
            // extract values between quotes using simple string operations
            String taskTitle = extractValue(message, "taskTitle");
            String assigneeName = extractValue(message, "assigneeName");
            String assigneeEmail = extractValue(message, "assigneeEmail");
            String projectName = extractValue(message, "projectName");

            log.info("=== NOTIFICATION ===");
            log.info("Task '{}' has been assigned to {}", taskTitle, assigneeName);
            log.info("Project: {}", projectName);
            log.info("Assignee email: {}", assigneeEmail);
            log.info("===================");

        } catch (Exception e) {
            log.error("Failed to process task-assigned event: {}", e.getMessage());
        }
    }

    // simple helper to extract a string value from JSON
    // e.g. {"taskTitle":"Fix bug"} → "Fix bug"
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int start = json.indexOf(searchKey);
        if (start == -1) return "unknown";
        start += searchKey.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? "unknown" : json.substring(start, end);
    }
}