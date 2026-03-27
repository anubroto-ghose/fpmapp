package com.fpm.controller;

import com.fpm.dto.ApprovalRequestStatusHistoryDTO;
import com.fpm.service.ApprovalRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8755 - Endpoint to get status history with graceful handling of incomplete/missing data
    @GetMapping("/{id}/status-history")
    public ResponseEntity<ApprovalRequestStatusHistoryDTO> getStatusHistory(@PathVariable("id") Long approvalRequestId) {
        ApprovalRequestStatusHistoryDTO dto = approvalRequestService.getStatusHistoryByApprovalRequestId(approvalRequestId);

        if (dto.getStatusHistoryEntries().isEmpty()) {
            // Inform user if no data available
            // TODO: Consider adding a message field in DTO or use HTTP headers for info
        }

        return ResponseEntity.ok(dto);
    }

}
