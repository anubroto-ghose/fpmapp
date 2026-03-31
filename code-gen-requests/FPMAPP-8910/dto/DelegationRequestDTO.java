package com.fpm.dto;

import java.time.LocalDateTime;

public class DelegationRequestDTO {

    private Long approverId;
    private Long delegateId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String permissions; // JSON or CSV string representing controlled permissions

    // STORY: FPMAPP-8910 - DTO for delegation request input from API/UI

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public Long getDelegateId() {
        return delegateId;
    }

    public void setDelegateId(Long delegateId) {
        this.delegateId = delegateId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
