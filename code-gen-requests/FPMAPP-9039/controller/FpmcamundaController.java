package com.fpm.controller;

import com.fpm.dto.ApprovalRequestDTO;
import com.fpm.service.FpmcamundaService;
import com.fpm.util.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fpmcamunda")
public class FpmcamundaController {

    @Autowired
    private FpmcamundaService fpmcamundaService;

    // STORY: FPMAPP-9039 - Enhanced approval API to support role-based routing, financial thresholds, and delegation parameters
    @PutMapping("/approvals")
    public ResponseEntity<ResponseWrapper> processApproval(@RequestBody ApprovalRequestDTO approvalRequest) {
        ResponseWrapper response = fpmcamundaService.processApproval(approvalRequest);
        return ResponseEntity.ok(response);
    }
}
