package com.graduate.dto;

import com.graduate.model.ProjectStatus;

import java.util.Map;

public class DashboardStats {

    private long totalProjects;
    private long draftProjects;
    private long submittedProjects;
    private long underReviewProjects;
    private long approvedProjects;
    private long rejectedProjects;
    private long inProgressProjects;
    private long completedProjects;
    private Map<ProjectStatus, Long> statusBreakdown;

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getDraftProjects() {
        return draftProjects;
    }

    public void setDraftProjects(long draftProjects) {
        this.draftProjects = draftProjects;
    }

    public long getSubmittedProjects() {
        return submittedProjects;
    }

    public void setSubmittedProjects(long submittedProjects) {
        this.submittedProjects = submittedProjects;
    }

    public long getUnderReviewProjects() {
        return underReviewProjects;
    }

    public void setUnderReviewProjects(long underReviewProjects) {
        this.underReviewProjects = underReviewProjects;
    }

    public long getApprovedProjects() {
        return approvedProjects;
    }

    public void setApprovedProjects(long approvedProjects) {
        this.approvedProjects = approvedProjects;
    }

    public long getRejectedProjects() {
        return rejectedProjects;
    }

    public void setRejectedProjects(long rejectedProjects) {
        this.rejectedProjects = rejectedProjects;
    }

    public long getInProgressProjects() {
        return inProgressProjects;
    }

    public void setInProgressProjects(long inProgressProjects) {
        this.inProgressProjects = inProgressProjects;
    }

    public long getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(long completedProjects) {
        this.completedProjects = completedProjects;
    }

    public Map<ProjectStatus, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<ProjectStatus, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }
}
