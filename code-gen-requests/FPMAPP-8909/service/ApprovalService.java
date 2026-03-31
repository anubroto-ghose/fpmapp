package com.fpm.service;

import com.fpm.dto.ApprovalActionRequest;
import com.fpm.model.ApprovalRequest;

public interface ApprovalService {

    // STORY: FPMAPP-8909 - Enforce hierarchical role-based approval workflow
    ApprovalRequest processApprovalAction(ApprovalActionRequest actionRequest) throws Exception;

    // STORY: FPMAPP-8909 - Retrieve approval request with current approver role and status
    ApprovalRequest getApprovalRequestById(Long id) throws Exception;
}
