package com.fpm.controller;

import com.fpm.exception.PermissionDeniedException;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approval")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    // STORY: FPMAPP-8769 - Endpoint to approve request with permission check
    @PostMapping("/approve")
    public ResponseEntity<String> approve(@RequestBody ApprovalRequest request) {
        String username = getCurrentUsername(); // TODO: Implement method to get logged-in user
        try {
            approvalService.approveRequest(request, username);
            return ResponseEntity.ok("Approval successful.");
        } catch (PermissionDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: You do not have permission to approve this request.");
        } catch (Exception e) {
            // TODO: Add proper logging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing approval.");
        }
    }

    private String getCurrentUsername() {
        // TODO: Implement retrieval of current logged-in username from security context
        return "anonymousUser";
    }
}