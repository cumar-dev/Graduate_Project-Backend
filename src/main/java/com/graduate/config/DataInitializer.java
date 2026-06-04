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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository,
                                   ProjectRepository projectRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            createUser(userRepository, passwordEncoder,
                    "System Administrator", "admin@university.edu", "admin123",
                    Role.ADMIN, "Academic Affairs", null);

            User supervisor1 = createUser(userRepository, passwordEncoder,
                    "Dr. Sarah Johnson", "supervisor@university.edu", "super123",
                    Role.SUPERVISOR, "Computer Science", null);

            createUser(userRepository, passwordEncoder,
                    "Prof. Michael Chen", "mchen@university.edu", "super123",
                    Role.SUPERVISOR, "Information Technology", null);

            User student1 = createUser(userRepository, passwordEncoder,
                    "Alice Williams", "student@university.edu", "student123",
                    Role.STUDENT, "Computer Science", "CS2024001");

            User student2 = createUser(userRepository, passwordEncoder,
                    "Bob Martinez", "bob@university.edu", "student123",
                    Role.STUDENT, "Information Technology", "IT2024015");

            Project draftProject = new Project();
            draftProject.setTitle("AI-Powered Campus Navigation System");
            draftProject.setDescription("A mobile application using machine learning to help students navigate campus buildings.");
            draftProject.setTechStack("Python, TensorFlow, React Native, Firebase");
            draftProject.setStartDate(LocalDate.of(2026, 9, 1));
            draftProject.setEndDate(LocalDate.of(2027, 5, 30));
            draftProject.setNoOfStudents(2);
            draftProject.setSupervisorName(supervisor1.getFullName());
            draftProject.setSupervisorId(supervisor1.getId());
            draftProject.setPhone("+1-555-0101");
            draftProject.setEmail(supervisor1.getEmail());
            draftProject.setYear(2026);
            draftProject.setStatus(ProjectStatus.DRAFT);
            draftProject.setStudentId(student1.getId());
            projectRepository.save(draftProject);

            Project submittedProject = new Project();
            submittedProject.setTitle("Blockchain-Based Academic Credential Verification");
            submittedProject.setDescription("A decentralized system for verifying academic certificates using blockchain.");
            submittedProject.setTechStack("Solidity, Ethereum, Node.js, React");
            submittedProject.setStartDate(LocalDate.of(2026, 2, 1));
            submittedProject.setEndDate(LocalDate.of(2026, 12, 15));
            submittedProject.setNoOfStudents(3);
            submittedProject.setSupervisorName(supervisor1.getFullName());
            submittedProject.setSupervisorId(supervisor1.getId());
            submittedProject.setPhone("+1-555-0101");
            submittedProject.setEmail(supervisor1.getEmail());
            submittedProject.setYear(2026);
            submittedProject.setStatus(ProjectStatus.SUBMITTED);
            submittedProject.setStudentId(student2.getId());
            projectRepository.save(submittedProject);

            Project inProgressProject = new Project();
            inProgressProject.setTitle("Smart Energy Monitoring for University Buildings");
            inProgressProject.setDescription("IoT-based system to monitor and optimize energy consumption across campus.");
            inProgressProject.setTechStack("Arduino, MQTT, Java Spring Boot, Grafana");
            inProgressProject.setStartDate(LocalDate.of(2025, 9, 1));
            inProgressProject.setEndDate(LocalDate.of(2026, 6, 30));
            inProgressProject.setNoOfStudents(4);
            inProgressProject.setSupervisorName(supervisor1.getFullName());
            inProgressProject.setSupervisorId(supervisor1.getId());
            inProgressProject.setPhone("+1-555-0101");
            inProgressProject.setEmail(supervisor1.getEmail());
            inProgressProject.setYear(2025);
            inProgressProject.setStatus(ProjectStatus.IN_PROGRESS);
            inProgressProject.setStudentId(student1.getId());
            projectRepository.save(inProgressProject);

            System.out.println("=== SIU Graduate Project Management ===");
            System.out.println("Demo accounts seeded:");
            System.out.println("  Admin:      admin@university.edu / admin123");
            System.out.println("  Supervisor: supervisor@university.edu / super123");
            System.out.println("  Student:    student@university.edu / student123");
        };
    }

    private User createUser(UserRepository repo, PasswordEncoder encoder,
                            String name, String email, String password,
                            Role role, String department, String studentId) {
        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setRole(role);
        user.setDepartment(department);
        user.setStudentId(studentId);
        return repo.save(user);
    }
}
