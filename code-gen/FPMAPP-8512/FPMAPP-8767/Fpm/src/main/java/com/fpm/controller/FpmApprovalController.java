package com.fpm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fpm.dto.RejectionRequestDTO;
import com.fpm.service.AuditLogService;

@RestController
@RequestMapping("/api/approval")
public class FpmApprovalController {

    @Autowired
    private AuditLogService auditLogService;

    // STORY: FPMAPP-8767 - Handle rejection action and log audit entry with missing comments
    @PostMapping("/reject")
    public ResponseEntity<String> rejectRequest(@RequestBody RejectionRequestDTO rejectionRequest) {
        // TODO: Validate user permissions and request state

        String userId = rejectionRequest.getUserId();
        String comments = rejectionRequest.getComments();

        // Perform rejection logic here
        // TODO: Implement actual rejection processing

        // Record audit log for rejection
        auditLogService.recordRejectionAction(userId, comments);

        return ResponseEntity.ok("Request rejected and audit logged.");
    }
}