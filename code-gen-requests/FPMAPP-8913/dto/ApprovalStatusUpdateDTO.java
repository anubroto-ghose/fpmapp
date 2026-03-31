package com.fpm.dto;

import java.time.Instant;

public class ApprovalStatusUpdateDTO {

    private String userId;
    private String approvalRequestId;
    private String status; // e.g., PENDING, APPROVED, REJECTED
    private String role;
    private boolean delegated;
    private Instant timestamp;

    // STORY: FPMAPP-8913 - DTO for sending real-time approval status updates

    public ApprovalStatusUpdateDTO() {
    }

    public ApprovalStatusUpdateDTO(String userId, String approvalRequestId, String status, String role, boolean delegated, Instant timestamp) {
        this.userId = userId;
        this.approvalRequestId = approvalRequestId;
        this.status = status;
        this.role = role;
        this.delegated = delegated;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(String approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDelegated() {
        return delegated;
    }

    public void setDelegated(boolean delegated) {
        this.delegated = delegated;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
