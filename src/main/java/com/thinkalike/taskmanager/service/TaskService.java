package com.thinkalike.taskmanager.service;

import com.thinkalike.taskmanager.dto.TaskRequest;
import com.thinkalike.taskmanager.dto.TaskResponse;
import com.thinkalike.taskmanager.model.Task;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse getTaskById(Long id);

    List<TaskResponse> getAllTasks();

    List<TaskResponse> getTasksByProject(Long projectId);

    List<TaskResponse> getTasksByAssignee(Long assigneeId);

    List<TaskResponse> getTasksByProjectAndStatus(Long projectId, Task.TaskStatus status);

    TaskResponse updateTask(Long id, TaskRequest request);

    TaskResponse updateTaskStatus(Long id, Task.TaskStatus status);

    TaskResponse assignTask(Long taskId, Long userId);

    void deleteTask(Long id);
}
