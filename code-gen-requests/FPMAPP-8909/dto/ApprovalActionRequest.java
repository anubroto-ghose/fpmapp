package com.fpm.dto;

import jakarta.validation.constraints.NotNull;

public class ApprovalActionRequest {

    @NotNull
    private Long approvalRequestId;

    @NotNull
    private String action; // APPROVE or REJECT

    @NotNull
    private String userRole; // Role of the user performing the action

    // STORY: FPMAPP-8909 - DTO to capture approval action request with user role validation

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
