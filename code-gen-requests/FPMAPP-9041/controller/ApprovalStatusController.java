package com.fpm.controller;

import com.fpm.dto.ApprovalStatusDTO;
import com.fpm.service.ApprovalStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApprovalStatusController {

    @Autowired
    private ApprovalStatusService approvalStatusService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // STORY: FPMAPP-9041 - Provide REST API to fetch current approval status for a given request
    @GetMapping("/api/fpm/approval-status")
    public ApprovalStatusDTO getApprovalStatus(@RequestParam("request_id") String requestId) {
        return approvalStatusService.getCurrentApprovalStatus(requestId);
    }

    // STORY: FPMAPP-9041 - WebSocket endpoint for subscribing to real-time approval status updates
    // This method is optional if using STOMP over WebSocket with topic subscriptions
    // but included here for clarity.
    @MessageMapping("/approval-status/subscribe")
    @SendTo("/topic/approval-status")
    public ApprovalStatusDTO subscribeApprovalStatus(String requestId) {
        // TODO: Implement if needed to handle subscription messages
        return approvalStatusService.getCurrentApprovalStatus(requestId);
    }

    // STORY: FPMAPP-9041 - Method to push approval status updates to subscribed clients
    public void pushApprovalStatusUpdate(String requestId, ApprovalStatusDTO statusDTO) {
        messagingTemplate.convertAndSend("/topic/approval-status/" + requestId, statusDTO);
    }

}
