package com.graduate.controller;

import com.graduate.dto.AdminUpdateRequest;
import com.graduate.dto.AssignSupervisorRequest;
import com.graduate.dto.ProjectRequest;
import com.graduate.dto.ProjectResponse;
import com.graduate.dto.SystemActivityResponse;
import com.graduate.service.AdminService;
import com.graduate.service.ProjectService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ProjectService projectService;

    public AdminController(AdminService adminService, ProjectService projectService) {
        this.adminService = adminService;
        this.projectService = projectService;
    }

    @GetMapping("/activity")
    public ResponseEntity<SystemActivityResponse> getSystemActivity() {
        return ResponseEntity.ok(adminService.getSystemActivity());
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(adminService.getAllProjects());
    }

    @PutMapping("/projects/{id}/assign-supervisor")
    public ResponseEntity<ProjectResponse> assignSupervisor(@PathVariable String id,
                                                              @Valid @RequestBody AssignSupervisorRequest request) {
        return ResponseEntity.ok(adminService.assignSupervisor(id, request));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String id,
                                                         @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateProject(id, request));
    }

    @PutMapping("/projects/{id}/details")
    public ResponseEntity<ProjectResponse> updateProjectDetails(@PathVariable String id,
                                                                @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProjectAsAdmin(id, request));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        adminService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
