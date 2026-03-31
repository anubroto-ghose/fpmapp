package com.fpm.controller;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fpmcamunda")
public class FpmCamundaController {

    @Autowired
    private ApprovalService approvalService;

    // STORY: FPMAPP-9033 - Modified POST endpoint to handle role-based approval workflow with delegation
    @PostMapping("/approvals")
    public ResponseEntity<Map<String, String>> handleApproval(@RequestBody ApprovalRequestDTO request) {
        Map<String, String> result = approvalService.processApproval(request);
        return ResponseEntity.ok(result);
    }
}
