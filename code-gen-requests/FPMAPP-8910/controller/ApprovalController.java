package com.fpm.controller;

import com.fpm.dto.DelegationRequestDTO;
import com.fpm.exception.AuthorizationException;
import com.fpm.model.User;
import com.fpm.service.ApprovalService;
import com.fpm.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    // STORY: FPMAPP-8910 - Endpoint to delegate approval authority
    @PostMapping("/delegate")
    public ResponseEntity<String> delegateApproval(@Valid @RequestBody DelegationRequestDTO delegationRequest) {
        User currentUser = SecurityUtil.getCurrentUser();
        try {
            approvalService.delegateApprovalAuthority(currentUser, delegationRequest);
            return ResponseEntity.ok("Delegation created successfully");
        } catch (AuthorizationException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

}
