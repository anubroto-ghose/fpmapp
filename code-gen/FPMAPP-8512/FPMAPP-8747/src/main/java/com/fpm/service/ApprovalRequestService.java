package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8747 - Update approval status and trigger notification
    ApprovalRequest updateApprovalStatus(Long approvalRequestId, String newStatus, String approverName);
}