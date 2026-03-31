package com.fpm.dto;

// STORY: FPMAPP-9038 - DTO for approval response with notification and delegation status
public class ApprovalResponseDTO {
    private String notificationStatus;
    private String delegationStatus;

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getDelegationStatus() {
        return delegationStatus;
    }

    public void setDelegationStatus(String delegationStatus) {
        this.delegationStatus = delegationStatus;
    }
}