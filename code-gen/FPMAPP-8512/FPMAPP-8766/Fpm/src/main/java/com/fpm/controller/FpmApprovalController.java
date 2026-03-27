package com.fpm.controller;

import com.fpm.exception.PermissionDeniedException;
import com.fpm.service.FpmApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approvals")
public class FpmApprovalController {

    @Autowired
    private FpmApprovalService fpmApprovalService;

    // STORY: FPMAPP-8766 - Reject action denied and no audit log created if user lacks approval permissions
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long requestId) {
        try {
            fpmApprovalService.rejectRequest(requestId);
            return ResponseEntity.ok().body("Request rejected successfully.");
        } catch (PermissionDeniedException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }
}
