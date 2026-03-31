package com.fpm.dto;

import java.util.Map;

public class ApprovalStatusResponse {
    private String status;
    private String currentApproverRole;
    private Map<String, Object> delegationInfo;

    // STORY: FPMAPP-9035 - DTO for approval status response

    public ApprovalStatusResponse() {
    }

    public ApprovalStatusResponse(String status, String currentApproverRole, Map<String, Object> delegationInfo) {
        this.status = status;
        this.currentApproverRole = currentApproverRole;
        this.delegationInfo = delegationInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentApproverRole() {
        return currentApproverRole;
    }

    public void setCurrentApproverRole(String currentApproverRole) {
        this.currentApproverRole = currentApproverRole;
    }

    public Map<String, Object> getDelegationInfo() {
        return delegationInfo;
    }

    public void setDelegationInfo(Map<String, Object> delegationInfo) {
        this.delegationInfo = delegationInfo;
    }
}
