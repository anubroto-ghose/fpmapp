package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalRequestStatusUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApprovalRequestStatusUpdateServiceImpl implements ApprovalRequestStatusUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ApprovalRequestStatusUpdateServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // STORY: FPMAPP-8743 - Broadcast multiple approval request status updates simultaneously
    @Override
    public void broadcastStatusUpdates(List<ApprovalRequestStatusUpdateDTO> updates) {
        // TODO: Consider batching or throttling if needed for performance
        for (ApprovalRequestStatusUpdateDTO update : updates) {
            // Send each update to a topic subscribed by UI clients
            messagingTemplate.convertAndSend(
                "/topic/approval-requests/status-updates",
                update
            );
        }
    }
}