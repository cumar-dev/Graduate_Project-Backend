package com.graduate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AssignSupervisorRequest {

    private String supervisorId;

    @Size(max = 100)
    private String supervisorName;

    @Size(max = 20)
    private String phone;

    @Email
    @Size(max = 100)
    private String email;

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
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
}
