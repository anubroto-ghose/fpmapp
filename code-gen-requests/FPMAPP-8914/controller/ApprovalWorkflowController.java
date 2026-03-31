package com.fpm.controller;

import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalWorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/approval")
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;

    public ApprovalWorkflowController(ApprovalWorkflowService approvalWorkflowService) {
        this.approvalWorkflowService = approvalWorkflowService;
    }

    // STORY: FPMAPP-8914 - Endpoint to submit a new approval request
    @PostMapping("/submit")
    public ResponseEntity<ApprovalRequest> submitApprovalRequest(@RequestBody ApprovalRequest request) {
        ApprovalRequest savedRequest = approvalWorkflowService.submitApprovalRequest(request);
        return new ResponseEntity<>(savedRequest, HttpStatus.CREATED);
    }

    // STORY: FPMAPP-8914 - Endpoint to approve an approval request
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long id,
            @RequestParam String approverRole,
            @RequestParam String approverName,
            @RequestParam(required = false) String comments) {
        try {
            Optional<ApprovalRequest> updatedRequest = approvalWorkflowService.approveRequest(id, approverRole, approverName, comments);
            return updatedRequest
                    .map(request -> ResponseEntity.ok(request))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // STORY: FPMAPP-8914 - Endpoint to reject an approval request
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long id,
            @RequestParam String approverRole,
            @RequestParam String approverName,
            @RequestParam(required = false) String comments) {
        try {
            Optional<ApprovalRequest> updatedRequest = approvalWorkflowService.rejectRequest(id, approverRole, approverName, comments);
            return updatedRequest
                    .map(request -> ResponseEntity.ok(request))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // STORY: FPMAPP-8914 - Endpoint to get approval request details
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalRequest> getApprovalRequest(@PathVariable Long id) {
        Optional<ApprovalRequest> request = approvalWorkflowService.getApprovalRequest(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
