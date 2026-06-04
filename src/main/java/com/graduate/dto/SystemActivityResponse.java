package com.graduate.dto;

import com.graduate.model.ProjectStatus;

import java.util.Map;

public class SystemActivityResponse {

    private long totalStudents;
    private long totalSupervisors;
    private long totalAdmins;
    private long totalProjects;
    private long projectsWithoutSupervisor;
    private long recentlySubmitted;
    private long recentlyUpdated;
    private DashboardStats projectStats;
    private Map<ProjectStatus, Long> statusBreakdown;

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalSupervisors() {
        return totalSupervisors;
    }

    public void setTotalSupervisors(long totalSupervisors) {
        this.totalSupervisors = totalSupervisors;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getProjectsWithoutSupervisor() {
        return projectsWithoutSupervisor;
    }

    public void setProjectsWithoutSupervisor(long projectsWithoutSupervisor) {
        this.projectsWithoutSupervisor = projectsWithoutSupervisor;
    }

    public long getRecentlySubmitted() {
        return recentlySubmitted;
    }

    public void setRecentlySubmitted(long recentlySubmitted) {
        this.recentlySubmitted = recentlySubmitted;
    }

    public long getRecentlyUpdated() {
        return recentlyUpdated;
    }

    public void setRecentlyUpdated(long recentlyUpdated) {
        this.recentlyUpdated = recentlyUpdated;
    }

    public DashboardStats getProjectStats() {
        return projectStats;
    }

    public void setProjectStats(DashboardStats projectStats) {
        this.projectStats = projectStats;
    }

    public Map<ProjectStatus, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<ProjectStatus, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }
}
