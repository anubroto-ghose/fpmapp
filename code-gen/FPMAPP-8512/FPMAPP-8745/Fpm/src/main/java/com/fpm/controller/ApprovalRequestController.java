package com.fpm.controller;

import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    // STORY: FPMAPP-8745 - Endpoint to update approval request status with notification control
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id, @RequestParam String status) {
        approvalRequestService.updateApprovalRequestStatus(id, status);
        return ResponseEntity.ok().build();
    }

    // TODO: other existing endpoints
}
