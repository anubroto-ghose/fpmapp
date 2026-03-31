package com.fpm.controller;

import com.fpm.dto.DelegationRequestDTO;
import com.fpm.model.Delegation;
import com.fpm.service.ApprovalService;
import jakarta.validation.Valid;
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

    // STORY: FPMAPP-8910 - API endpoint to create a delegation of approval authority
    @PostMapping("/delegate")
    public ResponseEntity<Delegation> delegateApprovalAuthority(@Valid @RequestBody DelegationRequestDTO delegationRequest) {
        Delegation delegation = approvalService.createDelegation(delegationRequest);
        return new ResponseEntity<>(delegation, HttpStatus.CREATED);
    }

    // TODO: Add endpoints to retrieve delegation history, revoke delegation, etc. if needed

}
