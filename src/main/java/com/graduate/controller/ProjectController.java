package com.graduate.controller;

import com.graduate.dto.DashboardStats;
import com.graduate.dto.ProjectRequest;
import com.graduate.dto.ProjectResponse;
import com.graduate.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects() {
        return ResponseEntity.ok(projectService.getProjectsForCurrentUser());
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats() {
        return ResponseEntity.ok(projectService.getDashboardStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
}

@RestController
@RequestMapping("/api/student/projects")
class StudentProjectController {

    private final ProjectService projectService;

    StudentProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String id,
                                                         @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ProjectResponse> submitProject(@PathVariable String id) {
        return ResponseEntity.ok(projectService.submitProject(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}

@RestController
@RequestMapping("/api/supervisor/projects")
class SupervisorProjectController {

    private final ProjectService projectService;

    SupervisorProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ProjectResponse>> getPendingReviews() {
        return ResponseEntity.ok(projectService.getPendingReviewsForSupervisor());
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ProjectResponse> startReview(@PathVariable String id) {
        return ResponseEntity.ok(projectService.startReview(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ProjectResponse> approveProject(@PathVariable String id) {
        return ResponseEntity.ok(projectService.approveProject(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ProjectResponse> rejectProject(@PathVariable String id) {
        return ResponseEntity.ok(projectService.rejectProject(id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ProjectResponse> markInProgress(@PathVariable String id) {
        return ResponseEntity.ok(projectService.markInProgress(id));
    }
}
