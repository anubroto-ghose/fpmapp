package com.fpm.service;

import com.fpm.dto.NotificationDTO;

public interface NotificationService {

    // STORY: FPMAPP-8771 - Create notification for pending approval request
    void createPendingApprovalNotification(Long userId, Long approvalRequestId, String approvalRequestTitle);

}
