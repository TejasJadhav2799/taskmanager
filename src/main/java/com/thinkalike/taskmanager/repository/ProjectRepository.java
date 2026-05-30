package com.thinkalike.taskmanager.repository;

import com.thinkalike.taskmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // find all projects owned by a specific user
    // generates: SELECT * FROM projects WHERE owner_id = ?
    List<Project> findByOwnerId(Long ownerId);

    // find all projects with a specific status
    // generates: SELECT * FROM projects WHERE status = ?
    List<Project> findByStatus(Project.ProjectStatus status);

    // find all active projects for a specific owner
    // generates: SELECT * FROM projects WHERE owner_id = ? AND status = ?
    List<Project> findByOwnerIdAndStatus(Long ownerId, Project.ProjectStatus status);
}
