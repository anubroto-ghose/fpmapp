package com.fpm.controller;

import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // STORY: FPMAPP-8738 - Handle WebSocket disconnect to prevent status updates being pushed
    public void onWebSocketDisconnect(String sessionId) {
        // TODO: Implement logic to mark the WebSocket session as disconnected
        approvalRequestService.markSessionDisconnected(sessionId);
    }

    // STORY: FPMAPP-8738 - Send status update only if WebSocket connection is active
    public void sendStatusUpdateIfConnected(Long approvalRequestId, String status) {
        if (approvalRequestService.isSessionConnected()) {
            // TODO: Send real-time status update via WebSocket
            messagingTemplate.convertAndSend("/topic/approval-status", new ApprovalStatusUpdateDTO(approvalRequestId, status));
        } else {
            // Do not send update if WebSocket is disconnected
            // Optionally log or handle this case
        }
    }

    // DTO class for status update message
    public static class ApprovalStatusUpdateDTO {
        private Long approvalRequestId;
        private String status;

        public ApprovalStatusUpdateDTO(Long approvalRequestId, String status) {
            this.approvalRequestId = approvalRequestId;
            this.status = status;
        }

        public Long getApprovalRequestId() {
            return approvalRequestId;
        }

        public String getStatus() {
            return status;
        }
    }
}