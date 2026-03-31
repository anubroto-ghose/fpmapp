package com.fpm.service;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;

public interface ApprovalNotificationService {

    // STORY: FPMAPP-8917 - Send real-time notification to user about approval status update
    void notifyUserApprovalStatusUpdate(Long userId, ApprovalRequestStatusUpdateDTO updateDTO);

    // STORY: FPMAPP-8917 - Send email alert for approval status update
    void sendEmailNotification(Long userId, ApprovalRequestStatusUpdateDTO updateDTO);

}