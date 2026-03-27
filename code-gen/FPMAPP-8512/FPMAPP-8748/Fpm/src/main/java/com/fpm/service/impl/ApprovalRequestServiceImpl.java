package com.fpm.service.impl;

import com.fpm.service.ApprovalRequestService;
import com.fpm.model.ApprovalRequest;
import com.fpm.dto.ApprovalRequestUpdateDto;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final NotificationService notificationService;

    public ApprovalRequestServiceImpl(ApprovalRequestRepository approvalRequestRepository, NotificationService notificationService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8748 - Update approval request without triggering notification if status unchanged
    public ApprovalRequest updateApprovalRequest(Long requestId, ApprovalRequestUpdateDto updateDto) {
        Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            // TODO: throw appropriate exception (e.g. ResourceNotFoundException)
            throw new IllegalArgumentException("Approval request not found with id: " + requestId);
        }

        ApprovalRequest existingRequest = optionalRequest.get();

        String oldStatus = existingRequest.getApprovalStatus();
        String newStatus = updateDto.getApprovalStatus();

        // Update fields except approval status
        existingRequest.setComments(updateDto.getComments());
        // TODO: update other fields as needed

        // Check if approval status changed
        boolean statusChanged = (newStatus != null && !newStatus.equals(oldStatus));

        if (statusChanged) {
            existingRequest.setApprovalStatus(newStatus);
        }

        ApprovalRequest savedRequest = approvalRequestRepository.save(existingRequest);

        if (statusChanged) {
            // Trigger notification only if status changed
            notificationService.notifyApprovalStatusChange(savedRequest);
        }
        // else no notification triggered

        return savedRequest;
    }

    // TODO: other existing methods
}
