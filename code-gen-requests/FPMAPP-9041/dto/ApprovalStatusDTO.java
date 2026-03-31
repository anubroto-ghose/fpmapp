package com.fpm.dto;

import lombok.Data;

@Data
public class ApprovalStatusDTO {
    // STORY: FPMAPP-9041 - DTO representing approval status details
    private String requestId;
    private String status; // e.g. APPROVED, REJECTED, IN_PROGRESS
    private String currentStage; // e.g. Initial Review, Manager Approval
    private String delegationInfo; // e.g. delegated to user X
    private String pendingActions; // e.g. "Waiting for manager approval"
}
