package com.fpm.controller;

import com.fpm.dto.ApprovalStatusResponse;
import com.fpm.service.ApprovalStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FpmDetailController {

    @Autowired
    private ApprovalStatusService approvalStatusService;

    // STORY: FPMAPP-9046 - API endpoint to fetch current approval status for a given request
    @GetMapping("/api/fpm/requests/status")
    public ResponseEntity<ApprovalStatusResponse> getApprovalStatus(@RequestParam("request_id") String requestId) {
        ApprovalStatusResponse statusResponse = approvalStatusService.getApprovalStatusByRequestId(requestId);
        return ResponseEntity.ok(statusResponse);
    }
}
