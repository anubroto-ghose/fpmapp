package com.fpm.controller;

import com.fpm.dto.ApprovalActionRequest;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    // STORY: FPMAPP-8909 - Endpoint to process approval actions with role validation
    @PostMapping("/action")
    public ResponseEntity<?> processApprovalAction(@RequestBody ApprovalActionRequest actionRequest) {
        try {
            ApprovalRequest updatedRequest = approvalService.processApprovalAction(actionRequest);
            return ResponseEntity.ok(updatedRequest);
        } catch (SecurityException se) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(se.getMessage());
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing approval action.");
        }
    }

    // STORY: FPMAPP-8909 - Endpoint to get approval request details including current approver role and status
    @GetMapping("/{id}")
    public ResponseEntity<?> getApprovalRequest(@PathVariable Long id) {
        try {
            ApprovalRequest approvalRequest = approvalService.getApprovalRequestById(id);
            return ResponseEntity.ok(approvalRequest);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(iae.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving approval request.");
        }
    }
}
