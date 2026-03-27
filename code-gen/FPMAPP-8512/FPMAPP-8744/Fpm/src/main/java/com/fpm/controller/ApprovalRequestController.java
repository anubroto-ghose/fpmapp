package com.fpm.controller;

import com.fpm.dto.ApprovalRequestStatusUpdateDTO;
import com.fpm.model.ApprovalRequest;
import com.fpm.service.ApprovalRequestService;
import com.fpm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/approval-requests")
public class ApprovalRequestController {

    @Autowired
    private ApprovalRequestService approvalRequestService;

    @Autowired
    private NotificationService notificationService;

    // STORY: FPMAPP-8744 - Endpoint to update approval request status and trigger immediate notification on rejection
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApprovalRequestStatus(@PathVariable Long id, @RequestBody ApprovalRequestStatusUpdateDTO statusUpdateDTO) {
        ApprovalRequest updatedRequest = approvalRequestService.updateStatus(id, statusUpdateDTO.getStatus(), statusUpdateDTO.getApproverId());

        if ("Rejected".equalsIgnoreCase(updatedRequest.getStatus())) {
            // Prepare notification details
            String notificationMessage = String.format(
                    "Your approval request #%d has been %s by %s at %s.",
                    updatedRequest.getId(),
                    updatedRequest.getStatus(),
                    updatedRequest.getApproverName(),
                    Instant.now().toString()
            );

            // STORY: FPMAPP-8744 - Trigger notification immediately upon rejection status change
            notificationService.sendNotificationToUser(
                    updatedRequest.getRequesterId(),
                    "Approval Request Status Update",
                    notificationMessage
            );
        }

        return ResponseEntity.ok(updatedRequest);
    }
}