package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.ApprovalRequestService;
import com.fpm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private NotificationService notificationService;

    // STORY: FPMAPP-8746 - Update approval request status and trigger notifications on rejection
    @Override
    @Transactional
    public ApprovalRequest updateStatus(Long approvalRequestId, String newStatus, String approverId) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
            .orElseThrow(() -> new IllegalArgumentException("ApprovalRequest not found"));

        String oldStatus = approvalRequest.getStatus();
        approvalRequest.setStatus(newStatus);
        approvalRequestRepository.save(approvalRequest);

        // STORY: FPMAPP-8746 - Trigger notification immediately for each rejection status change
        if ("Rejected".equalsIgnoreCase(newStatus)) {
            notificationService.notifyRejectionStatusChange(approvalRequest, approverId);
        }

        return approvalRequest;
    }
}