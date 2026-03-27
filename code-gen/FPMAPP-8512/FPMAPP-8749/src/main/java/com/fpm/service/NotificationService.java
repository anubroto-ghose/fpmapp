package com.fpm.service;

import java.time.Instant;

public interface NotificationService {

    // STORY: FPMAPP-8749 - Send notification on approval status change to 'Rejected'
    void sendApprovalStatusChangeNotification(String requesterUsername, Long approvalRequestId, String newStatus, Instant timestamp, String approverUsername);

}