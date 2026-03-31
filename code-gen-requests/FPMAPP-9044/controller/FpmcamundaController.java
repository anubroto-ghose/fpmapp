package com.fpm.controller;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.dto.ApprovalRoutingResponseDTO;
import com.fpm.service.FpmcamundaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fpmcamunda/approvals")
public class FpmcamundaController {

    @Autowired
    private FpmcamundaService fpmcamundaService;

    // STORY: FPMAPP-9044 - Endpoint to submit approval requests and route based on role and financial thresholds
    @PostMapping("/route")
    public ResponseEntity<ApprovalRoutingResponseDTO> routeApprovalRequest(
            @Valid @RequestBody ApprovalRequestDTO approvalRequestDTO) {
        ApprovalRoutingResponseDTO response = fpmcamundaService.routeApproval(approvalRequestDTO);
        return ResponseEntity.ok(response);
    }

}