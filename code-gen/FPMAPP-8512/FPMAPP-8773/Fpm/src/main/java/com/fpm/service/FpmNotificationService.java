package com.fpm.service;

import com.fpm.dto.NotificationDto;

import java.util.List;

public interface FpmNotificationService {

    // STORY: FPMAPP-8773 - Retrieve notifications for a user including pending approvals
    List<NotificationDto> getNotificationsForUser(Long userId);

    // STORY: FPMAPP-8773 - Process approval action and update notification status
    NotificationDto processApprovalAction(Long approvalId, Long userId, String action);
}