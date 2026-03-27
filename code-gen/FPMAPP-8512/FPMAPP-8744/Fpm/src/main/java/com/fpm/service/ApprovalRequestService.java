package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8744 - Update approval request status and return updated entity
    ApprovalRequest updateStatus(Long approvalRequestId, String status, Long approverId);
}