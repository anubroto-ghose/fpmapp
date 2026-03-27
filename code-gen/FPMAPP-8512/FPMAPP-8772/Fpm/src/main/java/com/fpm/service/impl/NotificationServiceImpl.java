package com.fpm.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fpm.dto.NotificationDTO;
import com.fpm.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    // STORY: FPMAPP-8772 - Return empty list when no pending approval notifications exist
    @Override
    public List<NotificationDTO> getPendingApprovalNotificationsForUser(String userId) {
        // TODO: Implement actual retrieval logic from DB or cache
        // For now, return empty list to indicate no pending approvals
        return Collections.emptyList();
    }
}