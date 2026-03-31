package com.fpm.controller;

import com.fpm.dto.ApprovalStatusResponse;
import com.fpm.service.ApprovalStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FpmApprovalStatusController {

    @Autowired
    private ApprovalStatusService approvalStatusService;

    // STORY: FPMAPP-9035 - REST API to get current approval status for a given request
    @GetMapping("/fpm/approval/status")
    public ResponseEntity<ApprovalStatusResponse> getApprovalStatus(@RequestParam String requestId) {
        ApprovalStatusResponse response = approvalStatusService.getApprovalStatus(requestId);
        return ResponseEntity.ok(response);
    }
}
