package com.fpm.dto;

import java.math.BigDecimal;

// STORY: FPMAPP-9038 - DTO for approval request including delegation info
public class ApprovalRequestDTO {
    private String userRole;
    private BigDecimal financialAmount;
    private DelegationInfo delegationInfo;

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

    public DelegationInfo getDelegationInfo() {
        return delegationInfo;
    }

    public void setDelegationInfo(DelegationInfo delegationInfo) {
        this.delegationInfo = delegationInfo;
    }

    // Inner class for delegation details
    public static class DelegationInfo {
        private String targetUser;
        private String reason;

        public String getTargetUser() {
            return targetUser;
        }

        public void setTargetUser(String targetUser) {
            this.targetUser = targetUser;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}