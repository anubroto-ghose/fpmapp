package com.fpm.dto;

public class ApprovalRequestUpdateDto {

    private String approvalStatus;
    private String comments;

    // STORY: FPMAPP-8748 - DTO for updating approval request without triggering notification if status unchanged

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // TODO: add other fields if needed
}
