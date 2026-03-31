package com.fpm.dto;

import java.math.BigDecimal;

public class ApprovalActionRequest {

    private Long approvalRequestId;
    private String approverUsername;
    private String approverRole;
    private String action; // e.g. APPROVE or REJECT

    // STORY: FPMAPP-8909 - DTO for approval action request with role validation

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public String getApproverUsername() {
        return approverUsername;
    }

    public void setApproverUsername(String approverUsername) {
        this.approverUsername = approverUsername;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
