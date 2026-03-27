package com.fpm.controller;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.service.FpmApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
public class FpmApprovalRequestController {

    @Autowired
    private FpmApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8754 - Return empty list with no errors when no completed approval requests exist
    @GetMapping("/completed")
    public ResponseEntity<List<ApprovalRequestDTO>> getCompletedApprovalRequests() {
        List<ApprovalRequestDTO> completedRequests = approvalRequestService.getCompletedApprovalRequests();
        if (completedRequests == null || completedRequests.isEmpty()) {
            // Return empty list to indicate no completed approval requests
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(completedRequests);
    }
}