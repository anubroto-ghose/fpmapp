package com.fpm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalRequestStatusUpdateService {

    @Autowired
    private ApprovalRequestWebSocketService websocketService;

    // STORY: FPMAPP-8742 - Send status update only if WebSocket connection is active
    public void sendStatusUpdateToClients(Long approvalRequestId, String newStatus) {
        if (!websocketService.isWebSocketConnected()) {
            // Do not send real-time update if WebSocket is disconnected
            return;
        }

        // TODO: Implement actual push notification logic to clients
        // e.g., messagingTemplate.convertAndSend(...)
    }
}