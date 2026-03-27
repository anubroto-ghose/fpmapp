package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8746 - Update approval request status and trigger notifications on rejection
    ApprovalRequest updateStatus(Long approvalRequestId, String newStatus, String approverId);

}