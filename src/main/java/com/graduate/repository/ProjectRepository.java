package com.graduate.repository;

import com.graduate.model.Project;
import com.graduate.model.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {

    @Query("{ 'student_id': ?0 }")
    List<Project> findByStudentId(String studentId);

    @Query("{ 'supervisor_id': ?0 }")
    List<Project> findBySupervisorId(String supervisorId);

    List<Project> findBySupervisorNameIgnoreCase(String supervisorName);

    List<Project> findByEmailIgnoreCase(String email);

    List<Project> findByStatus(ProjectStatus status);

    long countByStatus(ProjectStatus status);

    long countBySupervisorNameIsNull();
}
