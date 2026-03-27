package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8749 - Change approval status and trigger notification immediately
    ApprovalRequest updateApprovalStatusToRejected(Long approvalRequestId, String approverUsername);

}