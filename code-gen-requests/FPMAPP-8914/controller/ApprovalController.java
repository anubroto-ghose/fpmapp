package com.fpm.controller;

import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    // STORY: FPMAPP-8914 - Endpoint to create approval request
    @PostMapping
    public ResponseEntity<ApprovalRequest> createApprovalRequest(@RequestParam Double requestValue, @RequestParam String requestedBy) {
        ApprovalRequest request = approvalService.createApprovalRequest(requestValue, requestedBy);
        return ResponseEntity.ok(request);
    }

    // STORY: FPMAPP-8914 - Endpoint to approve request with role validation
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestParam String approverRole) {
        try {
            ApprovalRequest updatedRequest = approvalService.approveRequest(id, approverRole);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalAccessException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // STORY: FPMAPP-8914 - Endpoint to reject request with role validation
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestParam String approverRole) {
        try {
            ApprovalRequest updatedRequest = approvalService.rejectRequest(id, approverRole);
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalAccessException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // STORY: FPMAPP-8914 - Endpoint to get approval request details
    @GetMapping("/{id}")
    public ResponseEntity<?> getApprovalRequest(@PathVariable Long id) {
        return approvalService.getApprovalRequest(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
