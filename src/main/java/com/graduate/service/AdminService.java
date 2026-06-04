package com.graduate.service;

import com.graduate.dto.AdminUpdateRequest;
import com.graduate.dto.AssignSupervisorRequest;
import com.graduate.dto.DashboardStats;
import com.graduate.dto.ProjectResponse;
import com.graduate.dto.SystemActivityResponse;
import com.graduate.exception.BadRequestException;
import com.graduate.exception.ForbiddenException;
import com.graduate.exception.ResourceNotFoundException;
import com.graduate.model.Project;
import com.graduate.model.ProjectStatus;
import com.graduate.model.Role;
import com.graduate.model.User;
import com.graduate.repository.ProjectRepository;
import com.graduate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public AdminService(ProjectRepository projectRepository,
                        UserRepository userRepository,
                        UserService userService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<ProjectResponse> getAllProjects() {
        assertAdmin();
        return projectRepository.findAll().stream().map(ProjectResponse::from).toList();
    }

    public SystemActivityResponse getSystemActivity() {
        assertAdmin();

        List<Project> allProjects = projectRepository.findAll();
        LocalDate weekAgo = LocalDate.now().minusDays(7);

        SystemActivityResponse activity = new SystemActivityResponse();
        activity.setTotalStudents(userRepository.countByRole(Role.STUDENT));
        activity.setTotalSupervisors(userRepository.countByRole(Role.SUPERVISOR));
        activity.setTotalAdmins(userRepository.countByRole(Role.ADMIN));
        activity.setTotalProjects(allProjects.size());
        activity.setProjectsWithoutSupervisor(projectRepository.countBySupervisorNameIsNull());
        activity.setRecentlySubmitted(
                allProjects.stream()
                        .filter(p -> p.getStatus() == ProjectStatus.SUBMITTED
                                || p.getStatus() == ProjectStatus.UNDER_REVIEW)
                        .count());
        activity.setRecentlyUpdated(
                allProjects.stream()
                        .filter(p -> p.getStartDate() != null && !p.getStartDate().isBefore(weekAgo))
                        .count());

        Map<ProjectStatus, Long> breakdown = new EnumMap<>(ProjectStatus.class);
        for (ProjectStatus status : ProjectStatus.values()) {
            breakdown.put(status, allProjects.stream().filter(p -> p.getStatus() == status).count());
        }
        activity.setStatusBreakdown(breakdown);
        activity.setProjectStats(buildStats(allProjects, breakdown));
        return activity;
    }

    public ProjectResponse assignSupervisor(String projectId, AssignSupervisorRequest request) {
        assertAdmin();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            throw new BadRequestException("Cannot reassign supervisor for completed projects");
        }

        if (request.getSupervisorId() != null) {
            User supervisor = userRepository.findById(request.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found"));
            if (supervisor.getRole() != Role.SUPERVISOR) {
                throw new BadRequestException("Selected user is not a supervisor");
            }
            project.setSupervisorId(supervisor.getId());
            project.setSupervisorName(supervisor.getFullName());
            project.setEmail(supervisor.getEmail());
            if (request.getPhone() != null) {
                project.setPhone(request.getPhone());
            }
        } else {
            if (request.getSupervisorName() == null || request.getSupervisorName().isBlank()) {
                throw new BadRequestException("Supervisor name is required");
            }
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new BadRequestException("Supervisor email is required");
            }
            project.setSupervisorId(null);
            project.setSupervisorName(request.getSupervisorName());
            project.setEmail(request.getEmail());
            project.setPhone(request.getPhone());
        }

        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse updateProject(String projectId, AdminUpdateRequest request) {
        assertAdmin();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (request.getSupervisorName() != null) {
            project.setSupervisorName(request.getSupervisorName());
        }
        if (request.getPhone() != null) {
            project.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            project.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getYear() != null) {
            project.setYear(request.getYear());
        }

        return ProjectResponse.from(projectRepository.save(project));
    }

    public void deleteProject(String projectId) {
        assertAdmin();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        projectRepository.delete(project);
    }

    private DashboardStats buildStats(List<Project> projects, Map<ProjectStatus, Long> breakdown) {
        DashboardStats stats = new DashboardStats();
        stats.setTotalProjects(projects.size());
        stats.setStatusBreakdown(breakdown);
        stats.setDraftProjects(breakdown.getOrDefault(ProjectStatus.DRAFT, 0L));
        stats.setSubmittedProjects(breakdown.getOrDefault(ProjectStatus.SUBMITTED, 0L));
        stats.setUnderReviewProjects(breakdown.getOrDefault(ProjectStatus.UNDER_REVIEW, 0L));
        stats.setApprovedProjects(breakdown.getOrDefault(ProjectStatus.APPROVED, 0L));
        stats.setRejectedProjects(breakdown.getOrDefault(ProjectStatus.REJECTED, 0L));
        stats.setInProgressProjects(breakdown.getOrDefault(ProjectStatus.IN_PROGRESS, 0L));
        stats.setCompletedProjects(breakdown.getOrDefault(ProjectStatus.COMPLETED, 0L));
        return stats;
    }

    private void assertAdmin() {
        if (userService.getCurrentUser().getRole() != Role.ADMIN) {
            throw new ForbiddenException("Admin access required");
        }
    }
}
