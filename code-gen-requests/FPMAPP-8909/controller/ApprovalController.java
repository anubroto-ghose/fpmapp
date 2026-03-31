package com.fpm.controller;

import com.fpm.dto.ApprovalActionRequest;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> processApprovalAction(@Valid @RequestBody ApprovalActionRequest approvalActionRequest) {
        try {
            ApprovalRequest updatedRequest = approvalService.processApprovalAction(approvalActionRequest);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // STORY: FPMAPP-8909 - Endpoint to get approval request details including current approver role and status
    @GetMapping("/{id}")
    public ResponseEntity<?> getApprovalRequest(@PathVariable Long id) {
        return approvalService
                .getApprovalRequestById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
