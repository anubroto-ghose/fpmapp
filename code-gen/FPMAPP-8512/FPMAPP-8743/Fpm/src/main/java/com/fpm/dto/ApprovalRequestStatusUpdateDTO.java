package com.fpm.dto;

import java.time.Instant;

public class ApprovalRequestStatusUpdateDTO {
    private Long approvalRequestId;
    private String status;
    private String currentApprover;
    private Instant timestamp;

    // STORY: FPMAPP-8743 - DTO for real-time approval request status updates

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentApprover() {
        return currentApprover;
    }

    public void setCurrentApprover(String currentApprover) {
        this.currentApprover = currentApprover;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}