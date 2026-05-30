package com.thinkalike.taskmanager.repository;

import com.thinkalike.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // all tasks in a specific project
    List<Task> findByProjectId(Long projectId);

    // all tasks assigned to a specific user
    List<Task> findByAssigneeId(Long assigneeId);

    // all tasks in a project with a specific status
    // generates: SELECT * FROM tasks WHERE project_id = ? AND status = ?
    List<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status);

    // all tasks assigned to a user with a specific status
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, Task.TaskStatus status);

    // custom JPQL query — finds overdue tasks for a user
    // JPQL uses class names (Task) not table names (tasks)
    // and field names (t.dueDate) not column names (due_date)
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.dueDate < :today AND t.status != :doneStatus")
    List<Task> findOverdueTasksForUser(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            @Param("doneStatus") Task.TaskStatus doneStatus
    );
}
