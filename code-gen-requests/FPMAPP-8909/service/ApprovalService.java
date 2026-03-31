package com.fpm.service;

import com.fpm.dto.ApprovalActionRequest;
import com.fpm.model.ApprovalRequest;

public interface ApprovalService {

    // STORY: FPMAPP-8909 - Method to process approval action with role-based validation
    ApprovalRequest processApprovalAction(ApprovalActionRequest approvalActionRequest) throws Exception;

    // STORY: FPMAPP-8909 - Method to determine initial approver role based on request value
    String determineInitialApproverRole(java.math.BigDecimal requestValue);
}
