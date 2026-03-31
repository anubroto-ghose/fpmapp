package com.fpm.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ApprovalRequestDTO {

    private String approvalId;
    private String userRole;
    private BigDecimal amount;
    private Map<String, Object> delegationInfo;

    // STORY: FPMAPP-9039 - DTO for approval API request supporting role, amount, and delegation info

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Map<String, Object> getDelegationInfo() {
        return delegationInfo;
    }

    public void setDelegationInfo(Map<String, Object> delegationInfo) {
        this.delegationInfo = delegationInfo;
    }
}
