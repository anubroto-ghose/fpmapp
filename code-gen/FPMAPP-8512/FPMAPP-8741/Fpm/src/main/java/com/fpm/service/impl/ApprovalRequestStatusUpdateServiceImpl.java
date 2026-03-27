package com.fpm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalRequestStatusUpdateService;

@Service
public class ApprovalRequestStatusUpdateServiceImpl implements ApprovalRequestStatusUpdateService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ApprovalRequestStatusUpdateServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // STORY: FPMAPP-8741 - Publish approval request status update to subscribed clients in real-time
    @Override
    public void publishStatusUpdate(ApprovalRequestStatusUpdateDTO statusUpdate) {
        // TODO: Add any business logic or validation if needed before sending
        messagingTemplate.convertAndSend("/topic/approval/status", statusUpdate);
    }
}
