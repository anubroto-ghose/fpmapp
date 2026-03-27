package com.fpm.service;

import com.fpm.model.ApprovalRequest;

public interface NotificationService {

    // STORY: FPMAPP-8745 - Send rejection notification only
    void sendRejectionNotification(ApprovalRequest approvalRequest);

    // TODO: other existing notification methods
}
