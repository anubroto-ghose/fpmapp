package com.fpm.controller;

import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8747 - Endpoint to change approval status and trigger notification
    @PutMapping("/{id}/status")
    public ResponseEntity<ApprovalRequest> changeApprovalStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            @RequestParam("approverName") String approverName) {

        ApprovalRequest updated = approvalRequestService.updateApprovalStatus(id, status, approverName);
        return ResponseEntity.ok(updated);
    }
}