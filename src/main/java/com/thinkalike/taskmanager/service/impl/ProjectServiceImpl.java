package com.thinkalike.taskmanager.service.impl;

import com.thinkalike.taskmanager.dto.ProjectRequest;
import com.thinkalike.taskmanager.dto.ProjectResponse;
import com.thinkalike.taskmanager.exception.ResourceNotFoundException;
import com.thinkalike.taskmanager.model.Project;
import com.thinkalike.taskmanager.model.User;
import com.thinkalike.taskmanager.repository.ProjectRepository;
import com.thinkalike.taskmanager.repository.UserRepository;
import com.thinkalike.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    // notice we inject UserRepository here too
    // the service needs it to look up the owner User object
    // this is perfectly fine — services can use multiple repositories

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        User owner = userRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(Project.ProjectStatus.ACTIVE) // new projects start as ACTIVE
                .owner(owner)
                .build();
        Project savedProject = projectRepository.save(project);
        return ProjectResponse.from(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "projects", key = "#id")
    // @Cacheable checks Redis first
    // key = "#id" means cache key is "projects::1", "projects::2" etc.
    // if found in Redis — return immediately, skip DB entirely
    // if not found — run the method, store result in Redis, return
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + id
                ));
        return ProjectResponse.from(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream().map(ProjectResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "projects-by-owner", key = "#ownerId")
    public List<ProjectResponse> getProjectsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("User not found with id: " + ownerId);
        }
        return projectRepository.findByOwnerId(ownerId).stream().map(ProjectResponse::from).toList();
    }

    @Override
    public List<ProjectResponse> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status).stream().map(ProjectResponse::from).toList();
    }

    @Override
    @CacheEvict(value = {"projects", "projects-by-owner"}, allEntries = true)
    // @CacheEvict removes stale cache entries when data changes
    // allEntries = true clears ALL entries in these caches
    // so next GET request goes to DB and gets fresh data
    public List<ProjectResponse> updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        // if owner is being changed, verify the new owner exists
        if (!project.getOwner().getId().equals(request.getOwnerId())) {
            User newOwner = userRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getOwnerId()));
            project.setOwner(newOwner);
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);
        return Collections.singletonList(ProjectResponse.from(updatedProject));
    }

    @Override
    public ProjectResponse updateProjectStatus(Long id, Project.ProjectStatus status) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setStatus(status);
        Project updatedProject = projectRepository.save(project);
        return ProjectResponse.from(updatedProject);
    }

    @Override
    @CacheEvict(value = {"projects", "projects-by-owner"}, allEntries = true)
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }
}
