package com.fpm.service.impl;

import com.fpm.dto.ApprovalActionRequest;
import com.fpm.model.ApprovalRequest;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.service.ApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;

    // Define approval thresholds
    private static final BigDecimal MANAGER_APPROVAL_LIMIT = new BigDecimal("10000");
    private static final BigDecimal DIRECTOR_APPROVAL_LIMIT = new BigDecimal("100000");

    public ApprovalServiceImpl(ApprovalRequestRepository approvalRequestRepository) {
        this.approvalRequestRepository = approvalRequestRepository;
    }

    // STORY: FPMAPP-8909 - Determine initial approver role based on request value thresholds
    @Override
    public String determineInitialApproverRole(BigDecimal requestValue) {
        if (requestValue.compareTo(DIRECTOR_APPROVAL_LIMIT) > 0) {
            return "DIRECTOR";
        } else if (requestValue.compareTo(MANAGER_APPROVAL_LIMIT) > 0) {
            return "MANAGER";
        } else {
            // Requests below or equal to manager limit may be auto-approved or handled differently
            return "MANAGER"; // TODO: Confirm business rule for low value requests
        }
    }

    // STORY: FPMAPP-8909 - Process approval action with role validation and update approval status
    @Override
    @Transactional
    public ApprovalRequest processApprovalAction(ApprovalActionRequest approvalActionRequest) throws Exception {
        Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(approvalActionRequest.getApprovalRequestId());
        if (optionalRequest.isEmpty()) {
            throw new Exception("Approval request not found");
        }

        ApprovalRequest approvalRequest = optionalRequest.get();

        // Validate user role against current approver role
        String currentApproverRole = approvalRequest.getCurrentApproverRole();
        String userRole = approvalActionRequest.getUserRole();

        if (!isUserAuthorizedToApprove(userRole, approvalRequest.getRequestValue())) {
            throw new Exception("User role '" + userRole + "' is not authorized to approve this request");
        }

        // Validate that user role matches current approver role
        if (!userRole.equalsIgnoreCase(currentApproverRole)) {
            throw new Exception("Approval action rejected: user role '" + userRole + "' does not match current approver role '" + currentApproverRole + "'");
        }

        // Process action
        String action = approvalActionRequest.getAction();
        if ("APPROVE".equalsIgnoreCase(action)) {
            // Update approval status
            approvalRequest.setApprovalStatus("APPROVED");
            approvalRequest.setCurrentApproverRole(null); // No further approvers
        } else if ("REJECT".equalsIgnoreCase(action)) {
            approvalRequest.setApprovalStatus("REJECTED");
            approvalRequest.setCurrentApproverRole(null);
        } else {
            throw new Exception("Invalid approval action: " + action);
        }

        approvalRequestRepository.save(approvalRequest);
        return approvalRequest;
    }

    // STORY: FPMAPP-8909 - Helper method to check if user role is authorized to approve based on request value
    private boolean isUserAuthorizedToApprove(String userRole, BigDecimal requestValue) {
        if (userRole == null) {
            return false;
        }
        userRole = userRole.toUpperCase();

        if (requestValue.compareTo(DIRECTOR_APPROVAL_LIMIT) > 0) {
            return "DIRECTOR".equals(userRole);
        } else if (requestValue.compareTo(MANAGER_APPROVAL_LIMIT) > 0) {
            return "MANAGER".equals(userRole) || "DIRECTOR".equals(userRole); // Directors can approve mid-tier too
        } else {
            // TODO: Define if managers or others can approve low value requests
            return "MANAGER".equals(userRole) || "DIRECTOR".equals(userRole);
        }
    }
}
