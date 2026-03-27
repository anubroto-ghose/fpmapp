package com.fpm.dto;

import java.time.LocalDateTime;

public class ApprovalRequestStatusUpdateDTO {

    private Long approvalRequestId;
    private String status;
    private String currentApprover;
    private LocalDateTime timestamp;

    public ApprovalRequestStatusUpdateDTO() {
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
