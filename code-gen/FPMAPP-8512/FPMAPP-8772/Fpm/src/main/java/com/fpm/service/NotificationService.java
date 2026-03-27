package com.fpm.service;

import java.util.List;

import com.fpm.dto.NotificationDTO;

public interface NotificationService {

    // STORY: FPMAPP-8772 - Check if there are any pending approval notifications for the user
    List<NotificationDTO> getPendingApprovalNotificationsForUser(String userId);

}