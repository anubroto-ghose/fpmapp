package com.fpm.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.ApprovalRequestService;
import com.fpm.service.NotificationService;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    // STORY: FPMAPP-8749 - Change approval status to 'Rejected' and trigger notification immediately
    public ApprovalRequest updateApprovalStatusToRejected(Long approvalRequestId, String approverUsername) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("ApprovalRequest not found with id: " + approvalRequestId));

        approvalRequest.setStatus("Rejected");
        approvalRequest.setApprover(approverUsername);
        approvalRequest.setStatusChangedTimestamp(Instant.now());

        ApprovalRequest updatedRequest = approvalRequestRepository.save(approvalRequest);

        // Trigger notification immediately
        notificationService.sendApprovalStatusChangeNotification(
                updatedRequest.getRequester(),
                updatedRequest.getId(),
                "Rejected",
                updatedRequest.getStatusChangedTimestamp(),
                approverUsername
        );

        return updatedRequest;
    }

}