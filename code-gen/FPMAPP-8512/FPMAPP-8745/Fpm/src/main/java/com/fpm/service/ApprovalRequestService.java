package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8745 - Update approval request status with notification control
    void updateApprovalRequestStatus(Long approvalRequestId, String newStatus);

    // TODO: other existing methods
}
