package com.fpm.controller;

import com.fpm.dto.StatusHistoryDTO;
import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8750 - Endpoint to get status history with placeholders for incomplete data
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<StatusHistoryDTO>> getStatusHistory(@PathVariable("id") Long approvalRequestId) {
        List<StatusHistoryDTO> statusHistory = approvalRequestService.getStatusHistoryByApprovalRequestId(approvalRequestId);
        return ResponseEntity.ok(statusHistory);
    }
}