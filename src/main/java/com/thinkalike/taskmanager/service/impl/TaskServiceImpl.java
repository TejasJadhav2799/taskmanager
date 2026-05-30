package com.thinkalike.taskmanager.service.impl;

import com.thinkalike.taskmanager.dto.TaskRequest;
import com.thinkalike.taskmanager.dto.TaskResponse;
import com.thinkalike.taskmanager.exception.ResourceNotFoundException;
import com.thinkalike.taskmanager.model.Project;
import com.thinkalike.taskmanager.model.Task;
import com.thinkalike.taskmanager.model.User;
import com.thinkalike.taskmanager.repository.ProjectRepository;
import com.thinkalike.taskmanager.repository.TaskRepository;
import com.thinkalike.taskmanager.repository.UserRepository;
import com.thinkalike.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        // verify project exists
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + request.getProjectId()
                ));

        // verify creator exists
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getCreatedById()
                ));

        // assignee is optional — only look up if provided
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assignee not found with id: " + request.getAssigneeId()
                    ));
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .project(project)
                .assignee(assignee)
                .createdBy(createdBy)
                .build();

        Task savedTask = taskRepository.save(task);
        return TaskResponse.from(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id
                ));
        return TaskResponse.from(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found with id: " + projectId
            );
        }
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(Long assigneeId) {
        if (!userRepository.existsById(assigneeId)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + assigneeId
            );
        }
        return taskRepository.findByAssigneeId(assigneeId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectAndStatus(Long projectId, Task.TaskStatus status) {
            return taskRepository.findByProjectIdAndStatus(projectId, status)
                    .stream()
                    .map(TaskResponse::from)
                    .toList();
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id
                ));

        // update basic fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        // update assignee if changed
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assignee not found with id: " + request.getAssigneeId()
                    ));
            task.setAssignee(assignee);
        } else {
            // if assigneeId is null — unassign the task
            task.setAssignee(null);
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, Task.TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + id
                ));
        task.setStatus(status);
        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    public TaskResponse assignTask(Long taskId, Long userId) {
        // dedicated assign method — clean and explicit
        // in Phase 2 we'll add: only project members can be assigned
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + taskId
                ));

        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId
                ));

        task.setAssignee(assignee);
        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Task not found with id: " + id
            );
        }
        taskRepository.deleteById(id);
    }
}
