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

    public ApprovalServiceImpl(ApprovalRequestRepository approvalRequestRepository) {
        this.approvalRequestRepository = approvalRequestRepository;
    }

    // STORY: FPMAPP-8909 - Process approval action with role validation and update approval status
    @Override
    @Transactional
    public ApprovalRequest processApprovalAction(ApprovalActionRequest actionRequest) throws Exception {
        Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(actionRequest.getApprovalRequestId());
        if (optionalRequest.isEmpty()) {
            throw new IllegalArgumentException("Approval request not found");
        }

        ApprovalRequest approvalRequest = optionalRequest.get();

        // Validate approver role against current approver role
        if (!approvalRequest.getCurrentApproverRole().equalsIgnoreCase(actionRequest.getApproverRole())) {
            throw new SecurityException("User role '" + actionRequest.getApproverRole() + "' is not authorized to approve this request. Expected role: " + approvalRequest.getCurrentApproverRole());
        }

        // Validate approval limits based on role and request value
        BigDecimal value = approvalRequest.getRequestValue();
        String role = actionRequest.getApproverRole().toLowerCase();

        boolean authorized = false;
        switch (role) {
            case "manager":
                // Managers can approve mid-tier requests (e.g. <= 10000)
                if (value.compareTo(new BigDecimal("10000")) <= 0) {
                    authorized = true;
                }
                break;
            case "director":
                // Directors can approve high-value requests (> 10000)
                if (value.compareTo(new BigDecimal("10000")) > 0) {
                    authorized = true;
                }
                break;
            default:
                // Other roles not authorized
                authorized = false;
        }

        if (!authorized) {
            throw new SecurityException("Approver role '" + actionRequest.getApproverRole() + "' is not authorized to approve request value " + value);
        }

        // Process action
        String action = actionRequest.getAction().toUpperCase();
        if ("APPROVE".equals(action)) {
            approvalRequest.setApprovalStatus("APPROVED");
            approvalRequest.setCurrentApproverRole(null); // approval complete
        } else if ("REJECT".equals(action)) {
            approvalRequest.setApprovalStatus("REJECTED");
            approvalRequest.setCurrentApproverRole(null); // approval complete
        } else {
            throw new IllegalArgumentException("Invalid approval action: " + action);
        }

        approvalRequestRepository.save(approvalRequest);
        return approvalRequest;
    }

    // STORY: FPMAPP-8909 - Retrieve approval request including current approver role and status
    @Override
    public ApprovalRequest getApprovalRequestById(Long id) throws Exception {
        return approvalRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found"));
    }
}
