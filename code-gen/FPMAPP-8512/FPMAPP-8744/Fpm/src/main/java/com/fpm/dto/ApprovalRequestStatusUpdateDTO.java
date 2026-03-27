package com.fpm.dto;

public class ApprovalRequestStatusUpdateDTO {

    private String status;
    private Long approverId;

    // STORY: FPMAPP-8744 - DTO for updating approval request status with approver info

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }
}