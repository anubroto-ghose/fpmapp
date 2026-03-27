package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.ApprovalRequestService;
import com.fpm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private NotificationService notificationService;

    // STORY: FPMAPP-8747 - Update approval status and trigger notification
    @Override
    @Transactional
    public ApprovalRequest updateApprovalStatus(Long approvalRequestId, String newStatus, String approverName) {
        Optional<ApprovalRequest> optional = approvalRequestRepository.findById(approvalRequestId);
        if (optional.isEmpty()) {
            // TODO: throw appropriate exception
            throw new IllegalArgumentException("ApprovalRequest not found with id: " + approvalRequestId);
        }

        ApprovalRequest approvalRequest = optional.get();
        approvalRequest.setStatus(newStatus);
        approvalRequest.setApproverName(approverName);
        approvalRequest.setStatusChangedAt(LocalDateTime.now());

        ApprovalRequest updated = approvalRequestRepository.save(approvalRequest);

        // Trigger notification immediately
        notificationService.notifyApprovalStatusChange(updated);

        return updated;
    }
}