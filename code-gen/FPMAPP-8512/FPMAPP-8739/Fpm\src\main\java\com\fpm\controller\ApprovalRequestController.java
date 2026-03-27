package com.fpm.controller;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8739 - endpoint to get approval requests with websocket connection status
    @GetMapping
    public ResponseEntity<List<ApprovalRequestStatusUpdateDTO>> getApprovalRequests(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "true") boolean websocketConnected) {

        List<ApprovalRequestStatusUpdateDTO> approvalRequests = approvalRequestService.getApprovalRequestsForUser(userId, websocketConnected);

        return ResponseEntity.ok(approvalRequests);
    }

    // TODO: other existing controller methods
}
