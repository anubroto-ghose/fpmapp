package com.fpm.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fpm.dto.ApprovalStatusUpdateDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ApprovalNotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    // STORY: FPMAPP-8917 - Send real-time approval status updates to subscribed users
    public void sendApprovalStatusUpdate(ApprovalStatusUpdateDTO update) {
        // TODO: Add any filtering or routing logic based on roles/delegation if needed
        messagingTemplate.convertAndSendToUser(
            update.getRequesterUsername(), "/queue/approval-status", update);
    }
}