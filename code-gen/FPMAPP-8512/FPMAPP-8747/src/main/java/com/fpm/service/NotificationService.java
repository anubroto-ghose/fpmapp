package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface NotificationService {

    // STORY: FPMAPP-8747 - Send notification immediately on approval status change
    void notifyApprovalStatusChange(ApprovalRequest approvalRequest);
}