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

    // STORY: FPMAPP-8745 - Update approval request status with notification control
    @Override
    @Transactional
    public void updateApprovalRequestStatus(Long approvalRequestId, String newStatus) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(approvalRequestId)
                .orElseThrow(() -> new IllegalArgumentException("ApprovalRequest not found with id: " + approvalRequestId));

        String oldStatus = approvalRequest.getStatus();
        approvalRequest.setStatus(newStatus);
        approvalRequestRepository.save(approvalRequest);

        // Only trigger notification if status changed to 'Rejected'
        if ("Rejected".equalsIgnoreCase(newStatus)) {
            // STORY: FPMAPP-8745 - Trigger notification only on rejection status
            notificationService.sendRejectionNotification(approvalRequest);
        }
        // else do not send any notification
    }

    // TODO: other existing methods
}
