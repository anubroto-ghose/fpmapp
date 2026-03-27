package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.ApprovalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    // STORY: FPMAPP-8744 - Update approval request status and approver details
    @Override
    public ApprovalRequest updateStatus(Long approvalRequestId, String status, Long approverId) {
        Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(approvalRequestId);
        if (optionalRequest.isEmpty()) {
            // TODO: throw appropriate exception (e.g. ResourceNotFoundException)
            throw new RuntimeException("ApprovalRequest not found with id: " + approvalRequestId);
        }

        ApprovalRequest approvalRequest = optionalRequest.get();
        approvalRequest.setStatus(status);
        approvalRequest.setApproverId(approverId);
        approvalRequest.setApproverName(fetchApproverNameById(approverId)); // TODO: implement this method or service call
        approvalRequest.setStatusUpdatedAt(Instant.now());

        return approvalRequestRepository.save(approvalRequest);
    }

    private String fetchApproverNameById(Long approverId) {
        // TODO: Implement logic to fetch approver's full name by their user ID
        // This could be a call to UserService or UserRepository
        return "Approver Name"; // placeholder
    }
}