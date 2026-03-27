package com.fpm.controller;

import com.fpm.dto.StatusHistoryDTO;
import com.fpm.service.FpmDetailService;
import com.fpm.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fpm/details")
public class FpmDetailController {

    private final FpmDetailService fpmDetailService;

    public FpmDetailController(FpmDetailService fpmDetailService) {
        this.fpmDetailService = fpmDetailService;
    }

    // STORY: FPMAPP-8751 - Handle fully missing status history data gracefully
    @GetMapping("/approval-requests/{requestId}/status-history")
    public ResponseEntity<?> getStatusHistoryByApprovalRequestId(@PathVariable Long requestId) {
        List<StatusHistoryDTO> statusHistory = fpmDetailService.getStatusHistoryByApprovalRequestId(requestId);
        if (statusHistory == null || statusHistory.isEmpty()) {
            // Return a clear message indicating no status history data is available
            return ResponseEntity.ok().body("No status history data is available for this approval request.");
        }
        return ResponseEntity.ok(statusHistory);
    }
}