package com.graduate.config;

import com.graduate.model.Project;
import com.graduate.model.ProjectStatus;
import com.graduate.model.Role;
import com.graduate.model.User;
import com.graduate.repository.ProjectRepository;
import com.graduate.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Links existing projects to supervisor users by email when supervisorId is missing.
 * Safe to run on every startup — only updates projects that need it.
 */
@Configuration
public class SupervisorLinkMigration {

    @Bean
    CommandLineRunner linkSupervisorIds(UserRepository userRepository,
                                        ProjectRepository projectRepository) {
        return args -> {
            linkByEmail(userRepository, projectRepository);
            linkByName(userRepository, projectRepository);
            normalizeLegacyDemoPendingProject(userRepository, projectRepository);
        };
    }

    private void linkByEmail(UserRepository userRepository, ProjectRepository projectRepository) {
        for (Project project : projectRepository.findAll()) {
            if (project.getSupervisorId() != null && !project.getSupervisorId().isBlank()) {
                continue;
            }
            if (project.getEmail() == null || project.getEmail().isBlank()) {
                continue;
            }

            userRepository.findByEmail(project.getEmail().trim().toLowerCase())
                    .filter(user -> user.getRole() == Role.SUPERVISOR)
                    .ifPresent(supervisor -> applySupervisorLink(project, supervisor, projectRepository));
        }
    }

    private void linkByName(UserRepository userRepository, ProjectRepository projectRepository) {
        for (Project project : projectRepository.findAll()) {
            if (project.getSupervisorId() != null && !project.getSupervisorId().isBlank()) {
                continue;
            }
            if (project.getSupervisorName() == null || project.getSupervisorName().isBlank()) {
                continue;
            }

            userRepository.findByRole(Role.SUPERVISOR).stream()
                    .filter(user -> user.getFullName().equalsIgnoreCase(project.getSupervisorName().trim()))
                    .findFirst()
                    .ifPresent(supervisor -> applySupervisorLink(project, supervisor, projectRepository));
        }
    }

    /** Reassign legacy seed pending project to the demo supervisor account. */
    private void normalizeLegacyDemoPendingProject(UserRepository userRepository,
                                                     ProjectRepository projectRepository) {
        userRepository.findByEmail("supervisor@university.edu").ifPresent(demoSupervisor -> {
            projectRepository.findAll().stream()
                    .filter(project -> "Blockchain-Based Academic Credential Verification".equals(project.getTitle()))
                    .filter(project -> project.getStatus() == ProjectStatus.SUBMITTED)
                    .filter(project -> "mchen@university.edu".equalsIgnoreCase(project.getEmail()))
                    .forEach(project -> {
                        project.setSupervisorId(demoSupervisor.getId());
                        project.setSupervisorName(demoSupervisor.getFullName());
                        project.setEmail(demoSupervisor.getEmail());
                        projectRepository.save(project);
                    });
        });
    }

    private void applySupervisorLink(Project project, User supervisor, ProjectRepository projectRepository) {
        project.setSupervisorId(supervisor.getId());
        if (project.getSupervisorName() == null || project.getSupervisorName().isBlank()) {
            project.setSupervisorName(supervisor.getFullName());
        }
        projectRepository.save(project);
    }
}
