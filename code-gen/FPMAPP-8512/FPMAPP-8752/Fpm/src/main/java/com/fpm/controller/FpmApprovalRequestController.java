package com.fpm.controller;

import com.fpm.dto.StatusHistoryEntryDTO;
import com.fpm.service.FpmApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
public class FpmApprovalRequestController {

    @Autowired
    private FpmApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8752 - Retrieve full status history for a completed approval request
    @GetMapping("/{requestId}/status-history")
    public ResponseEntity<List<StatusHistoryEntryDTO>> getStatusHistory(@PathVariable Long requestId) {
        List<StatusHistoryEntryDTO> statusHistory = approvalRequestService.getCompleteStatusHistory(requestId);
        return ResponseEntity.ok(statusHistory);
    }
}