package com.thinkalike.taskmanager.service;

import com.thinkalike.taskmanager.dto.ProjectRequest;
import com.thinkalike.taskmanager.dto.ProjectResponse;
import com.thinkalike.taskmanager.model.Project;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse getProjectById(Long id);

    List<ProjectResponse> getAllProjects();

    List<ProjectResponse> getProjectsByOwner(Long ownerId);

    List<ProjectResponse> getProjectsByStatus(Project.ProjectStatus status);

    List<ProjectResponse> updateProject(Long id, ProjectRequest request);

    ProjectResponse updateProjectStatus(Long id, Project.ProjectStatus status);

    void deleteProject(Long id);
}
