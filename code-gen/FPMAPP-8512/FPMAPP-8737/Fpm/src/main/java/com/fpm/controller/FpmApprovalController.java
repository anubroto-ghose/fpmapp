package com.fpm.controller;

import com.fpm.dto.ApprovalStatusUpdateDTO;
import com.fpm.service.FpmApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FpmApprovalController {

    @Autowired
    private FpmApprovalService approvalService;

    // STORY: FPMAPP-8737 - Endpoint to fetch current approval statuses for UI synchronization after reconnect
    @GetMapping("/api/approvals/statuses")
    public List<ApprovalStatusUpdateDTO> getCurrentApprovalStatuses() {
        return approvalService.getAllCurrentApprovalStatuses();
    }

    // STORY: FPMAPP-8737 - WebSocket endpoint to receive status updates from backend
    @MessageMapping("/approval/status/update")
    @SendTo("/topic/approval/status")
    public ApprovalStatusUpdateDTO broadcastStatusUpdate(ApprovalStatusUpdateDTO statusUpdate) {
        // TODO: Validate and process status update if needed
        return statusUpdate;
    }

}
