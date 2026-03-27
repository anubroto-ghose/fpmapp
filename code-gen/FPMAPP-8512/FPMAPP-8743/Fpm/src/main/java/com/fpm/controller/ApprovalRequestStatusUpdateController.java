package com.fpm.controller;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalRequestStatusUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ApprovalRequestStatusUpdateController {

    private final ApprovalRequestStatusUpdateService statusUpdateService;

    @Autowired
    public ApprovalRequestStatusUpdateController(ApprovalRequestStatusUpdateService statusUpdateService) {
        this.statusUpdateService = statusUpdateService;
    }

    // STORY: FPMAPP-8743 - Endpoint to receive and broadcast multiple simultaneous status updates
    @MessageMapping("/approval-requests/status-update")
    @SendTo("/topic/approval-requests/status-updates")
    public void handleStatusUpdates(List<ApprovalRequestStatusUpdateDTO> updates) {
        // This method can be called by internal services or WebSocket clients to push updates
        statusUpdateService.broadcastStatusUpdates(updates);
    }
}