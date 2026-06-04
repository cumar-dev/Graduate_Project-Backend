package com.graduate.dto;

import com.graduate.model.Project;
import com.graduate.model.ProjectStatus;

import java.time.LocalDate;

public class ProjectResponse {

    private String id;
    private String title;
    private String description;
    private String techStack;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer noOfStudents;
    private String supervisorName;
    private String supervisorId;
    private String phone;
    private String email;
    private Integer year;
    private ProjectStatus status;
    private String studentId;

    public static ProjectResponse from(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setTechStack(project.getTechStack());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setNoOfStudents(project.getNoOfStudents());
        response.setSupervisorName(project.getSupervisorName());
        response.setSupervisorId(project.getSupervisorId());
        response.setPhone(project.getPhone());
        response.setEmail(project.getEmail());
        response.setYear(project.getYear());
        response.setStatus(project.getStatus());
        response.setStudentId(project.getStudentId());
        return response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getNoOfStudents() {
        return noOfStudents;
    }

    public void setNoOfStudents(Integer noOfStudents) {
        this.noOfStudents = noOfStudents;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
