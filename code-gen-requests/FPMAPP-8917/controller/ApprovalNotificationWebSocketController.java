package com.fpm.controller;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ApprovalNotificationWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ApprovalNotificationService approvalNotificationService;

    // STORY: FPMAPP-8917 - Endpoint to send real-time approval status updates to requester
    public void sendApprovalStatusUpdateToUser(Long userId, ApprovalRequestStatusUpdateDTO updateDTO) {
        // Send message to specific user queue
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/approval-status", updateDTO);
    }

    // STORY: FPMAPP-8917 - Receive client subscription requests if needed (optional)
    @MessageMapping("/approval/status/request")
    @SendToUser("/queue/approval-status")
    public ApprovalRequestStatusUpdateDTO subscribeApprovalStatus(ApprovalRequestStatusUpdateDTO request) {
        // TODO: Implement if client sends subscription requests or ping
        return null;
    }
}