package com.fpm.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;

@Controller
public class ApprovalRequestWebSocketController {

    // STORY: FPMAPP-8741 - Endpoint to receive status update events and broadcast to subscribed clients
    @MessageMapping("/approval/status/update")
    @SendTo("/topic/approval/status")
    public ApprovalRequestStatusUpdateDTO broadcastStatusUpdate(ApprovalRequestStatusUpdateDTO statusUpdate) {
        // TODO: Validate and enrich statusUpdate if needed before broadcasting
        return statusUpdate;
    }
}
