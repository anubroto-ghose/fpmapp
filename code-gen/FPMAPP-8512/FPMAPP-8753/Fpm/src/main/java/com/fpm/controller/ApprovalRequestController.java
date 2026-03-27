package com.fpm.controller;

import com.fpm.dto.ApprovalStatusHistoryDTO;
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

    // STORY: FPMAPP-8753 - Endpoint to get full status history for a completed approval request
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<ApprovalStatusHistoryDTO>> getStatusHistory(@PathVariable("id") Long approvalRequestId) {
        List<ApprovalStatusHistoryDTO> history = approvalRequestService.getFullStatusHistory(approvalRequestId);
        return ResponseEntity.ok(history);
    }
}
