package com.fpm.dto;

import java.math.BigDecimal;
import java.util.Map;

// STORY: FPMAPP-9033 - DTO for approval request input
public class ApprovalRequestDTO {
    private String userRole;
    private BigDecimal financialAmount;
    private Map<String, Object> delegationInfo; // e.g. delegatedToUserId, delegationFlag

    // Getters and setters

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public BigDecimal getFinancialAmount() {
        return financialAmount;
    }

    public void setFinancialAmount(BigDecimal financialAmount) {
        this.financialAmount = financialAmount;
    }

    public Map<String, Object> getDelegationInfo() {
        return delegationInfo;
    }

    public void setDelegationInfo(Map<String, Object> delegationInfo) {
        this.delegationInfo = delegationInfo;
    }
}
