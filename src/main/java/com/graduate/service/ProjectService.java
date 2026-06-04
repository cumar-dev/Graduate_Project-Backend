package com.graduate.service;

import com.graduate.dto.DashboardStats;
import com.graduate.dto.ProjectRequest;
import com.graduate.dto.ProjectResponse;
import com.graduate.exception.BadRequestException;
import com.graduate.exception.ForbiddenException;
import com.graduate.exception.ResourceNotFoundException;
import com.graduate.model.Project;
import com.graduate.model.ProjectStatus;
import com.graduate.model.Role;
import com.graduate.model.User;
import com.graduate.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public List<ProjectResponse> getProjectsForCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return findProjectsForUser(currentUser).stream().map(ProjectResponse::from).toList();
    }

    public ProjectResponse getProjectById(String id) {
        Project project = findProject(id);
        assertCanView(project);
        return ProjectResponse.from(project);
    }

    public ProjectResponse createProject(ProjectRequest request) {
        User student = userService.getCurrentUser();
        if (student.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only students can register projects");
        }
        validateDates(request);

        Project project = new Project();
        applyProjectFields(project, request);
        project.setStudentId(student.getId());
        project.setStatus(ProjectStatus.DRAFT);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse updateProject(String id, ProjectRequest request) {
        Project project = findProject(id);
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only students can edit project content");
        }
        if (!currentUser.getId().equals(project.getStudentId())) {
            throw new ForbiddenException("You can only edit your own projects");
        }
        if (project.getStatus() != ProjectStatus.DRAFT && project.getStatus() != ProjectStatus.REJECTED) {
            throw new BadRequestException("Project can only be edited before approval (draft or rejected status)");
        }
        validateDates(request);
        applyProjectFields(project, request);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse updateProjectAsAdmin(String id, ProjectRequest request) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Admin access required");
        }
        Project project = findProject(id);
        validateDates(request);
        applyProjectFields(project, request);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public void deleteProject(String id) {
        Project project = findProject(id);
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            projectRepository.delete(project);
            return;
        }
        if (currentUser.getRole() != Role.STUDENT) {
            throw new ForbiddenException("You cannot delete this project");
        }
        if (!currentUser.getId().equals(project.getStudentId())) {
            throw new ForbiddenException("You can only delete your own projects");
        }
        if (project.getStatus() != ProjectStatus.DRAFT && project.getStatus() != ProjectStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected projects can be deleted");
        }
        projectRepository.delete(project);
    }

    public ProjectResponse submitProject(String id) {
        Project project = findProject(id);
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != Role.STUDENT || !currentUser.getId().equals(project.getStudentId())) {
            throw new ForbiddenException("Only the project owner can submit");
        }
        if (project.getStatus() != ProjectStatus.DRAFT && project.getStatus() != ProjectStatus.REJECTED) {
            throw new BadRequestException("Only draft or rejected projects can be submitted");
        }
        if (!hasSupervisorAssigned(project)) {
            throw new BadRequestException("Please assign a supervisor before submitting");
        }

        linkSupervisorIfPossible(project);
        if (project.getSupervisorId() == null || project.getSupervisorId().isBlank()) {
            throw new BadRequestException(
                    "Could not link this proposal to a supervisor. Please pick a supervisor from the list.");
        }

        project.setStatus(ProjectStatus.SUBMITTED);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse startReview(String id) {
        Project project = findProject(id);
        assertSupervisorAccess(project);

        if (project.getStatus() != ProjectStatus.SUBMITTED) {
            throw new BadRequestException("Only submitted projects can be moved to review");
        }
        project.setStatus(ProjectStatus.UNDER_REVIEW);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse approveProject(String id) {
        Project project = findProject(id);
        assertSupervisorAccess(project);

        if (project.getStatus() != ProjectStatus.SUBMITTED && project.getStatus() != ProjectStatus.UNDER_REVIEW) {
            throw new BadRequestException("Project must be submitted or under review to approve");
        }
        project.setStatus(ProjectStatus.APPROVED);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse rejectProject(String id) {
        Project project = findProject(id);
        assertSupervisorAccess(project);

        if (project.getStatus() != ProjectStatus.SUBMITTED && project.getStatus() != ProjectStatus.UNDER_REVIEW) {
            throw new BadRequestException("Project must be submitted or under review to reject");
        }
        project.setStatus(ProjectStatus.REJECTED);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public ProjectResponse markInProgress(String id) {
        Project project = findProject(id);
        assertSupervisorAccess(project);

        if (project.getStatus() != ProjectStatus.APPROVED) {
            throw new BadRequestException("Only approved projects can be marked in progress");
        }
        project.setStatus(ProjectStatus.IN_PROGRESS);
        return ProjectResponse.from(projectRepository.save(project));
    }

    public List<ProjectResponse> getPendingReviewsForSupervisor() {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.SUPERVISOR) {
            throw new ForbiddenException("Supervisor access required");
        }

        return findProjectsForUser(currentUser).stream()
                .filter(p -> p.getStatus() == ProjectStatus.SUBMITTED
                        || p.getStatus() == ProjectStatus.UNDER_REVIEW)
                .map(ProjectResponse::from)
                .toList();
    }

    public DashboardStats getDashboardStats() {
        User currentUser = userService.getCurrentUser();
        List<Project> projects = findProjectsForUser(currentUser);
        return buildStats(projects);
    }

    private DashboardStats buildStats(List<Project> projects) {
        DashboardStats stats = new DashboardStats();
        stats.setTotalProjects(projects.size());

        Map<ProjectStatus, Long> breakdown = new EnumMap<>(ProjectStatus.class);
        for (ProjectStatus status : ProjectStatus.values()) {
            long count = projects.stream().filter(p -> p.getStatus() == status).count();
            breakdown.put(status, count);
        }
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

    private List<Project> findProjectsForUser(User user) {
        return switch (user.getRole()) {
            case STUDENT -> projectRepository.findByStudentId(user.getId());
            case SUPERVISOR -> findProjectsForSupervisor(user);
            case ADMIN -> projectRepository.findAll();
        };
    }

    private List<Project> findProjectsForSupervisor(User supervisor) {
        Map<String, Project> assigned = new java.util.LinkedHashMap<>();

        for (Project project : projectRepository.findBySupervisorId(supervisor.getId())) {
            assigned.put(project.getId(), project);
        }
        for (Project project : projectRepository.findByEmailIgnoreCase(supervisor.getEmail())) {
            assigned.putIfAbsent(project.getId(), project);
        }
        for (Project project : projectRepository.findBySupervisorNameIgnoreCase(supervisor.getFullName())) {
            assigned.putIfAbsent(project.getId(), project);
        }

        return List.copyOf(assigned.values());
    }

    private boolean matchesSupervisor(Project project, User supervisor) {
        if (project.getSupervisorId() != null
                && project.getSupervisorId().equals(supervisor.getId())) {
            return true;
        }
        if (project.getEmail() != null
                && project.getEmail().equalsIgnoreCase(supervisor.getEmail())) {
            return true;
        }
        return project.getSupervisorName() != null
                && project.getSupervisorName().equalsIgnoreCase(supervisor.getFullName());
    }

    private boolean hasSupervisorAssigned(Project project) {
        if (project.getSupervisorId() != null && !project.getSupervisorId().isBlank()) {
            return true;
        }
        return project.getSupervisorName() != null && !project.getSupervisorName().isBlank();
    }

    private void applyProjectFields(Project project, ProjectRequest request) {
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setTechStack(request.getTechStack());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setNoOfStudents(request.getNoOfStudents());
        applySupervisorFields(project, request);
        project.setYear(request.getYear());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
    }

    private void applySupervisorFields(Project project, ProjectRequest request) {
        if (request.getPhone() != null) {
            project.setPhone(request.getPhone());
        }

        userService.resolveSupervisor(
                        request.getSupervisorId(),
                        request.getEmail(),
                        request.getSupervisorName())
                .ifPresentOrElse(
                        supervisor -> applySupervisorUser(project, supervisor),
                        () -> applyManualSupervisorFields(project, request)
                );
    }

    private void applySupervisorUser(Project project, User supervisor) {
        project.setSupervisorId(supervisor.getId());
        project.setSupervisorName(supervisor.getFullName());
        project.setEmail(supervisor.getEmail());
    }

    private void applyManualSupervisorFields(Project project, ProjectRequest request) {
        project.setSupervisorName(request.getSupervisorName());
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            project.setEmail(request.getEmail().trim().toLowerCase());
        }
        linkSupervisorIfPossible(project);
    }

    private void linkSupervisorIfPossible(Project project) {
        userService.resolveSupervisor(project.getSupervisorId(), project.getEmail(), project.getSupervisorName())
                .ifPresent(supervisor -> applySupervisorUser(project, supervisor));
    }

    private void validateDates(ProjectRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be on or after start date");
        }
    }

    private Project findProject(String id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    private void assertCanView(Project project) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.STUDENT && currentUser.getId().equals(project.getStudentId())) {
            return;
        }
        if (currentUser.getRole() == Role.SUPERVISOR && matchesSupervisor(project, currentUser)) {
            return;
        }
        throw new ForbiddenException("You do not have access to this project");
    }

    private void assertSupervisorAccess(Project project) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() != Role.SUPERVISOR && currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Supervisor access required");
        }
        if (currentUser.getRole() == Role.SUPERVISOR && !matchesSupervisor(project, currentUser)) {
            throw new ForbiddenException("This project is not assigned to you");
        }
    }
}
