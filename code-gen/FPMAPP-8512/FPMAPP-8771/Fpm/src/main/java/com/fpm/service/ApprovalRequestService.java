package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface ApprovalRequestService {

    // STORY: FPMAPP-8771 - Submit approval request and trigger notification
    ApprovalRequest submitApprovalRequest(ApprovalRequest approvalRequest);

}
