package com.fpm.controller;

import com.fpm.dto.ApprovalRequestUpdateDto;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;

    public ApprovalRequestController(ApprovalRequestService approvalRequestService) {
        this.approvalRequestService = approvalRequestService;
    }

    @PutMapping("/{id}")
    // STORY: FPMAPP-8748 - Update approval request without triggering notification if status unchanged
    public ResponseEntity<ApprovalRequest> updateApprovalRequest(@PathVariable("id") Long id,
                                                                 @RequestBody ApprovalRequestUpdateDto updateDto) {
        ApprovalRequest updatedRequest = approvalRequestService.updateApprovalRequest(id, updateDto);
        return ResponseEntity.ok(updatedRequest);
    }

    // TODO: other existing endpoints
}
