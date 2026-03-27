package com.fpm.controller;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.FpmApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FpmApprovalController {

    @Autowired
    private FpmApprovalService approvalService;

    // STORY: FPMAPP-8740 - Provide endpoint to fetch latest approval request statuses after WebSocket reconnection
    @GetMapping("/api/approvals/active")
    public List<ApprovalRequestStatusUpdateDTO> getActiveApprovalRequests() {
        return approvalService.getActiveApprovalRequests();
    }

    // STORY: FPMAPP-8740 - WebSocket endpoint to send real-time approval status updates
    @MessageMapping("/approval/status/update")
    @SendTo("/topic/approval/status")
    public ApprovalRequestStatusUpdateDTO sendStatusUpdate(ApprovalRequestStatusUpdateDTO update) {
        // TODO: Validate and process incoming status update if needed
        return update;
    }

}
