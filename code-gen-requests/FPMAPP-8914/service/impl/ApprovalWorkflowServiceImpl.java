package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.model.ApprovalRole;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.ApprovalRoleRepository;
import com.fpm.service.ApprovalWorkflowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ApprovalWorkflowServiceImpl implements ApprovalWorkflowService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalRoleRepository approvalRoleRepository;

    // Define role hierarchy order (lowest to highest)
    private static final List<String> ROLE_HIERARCHY = List.of("MANAGER", "DIRECTOR");

    public ApprovalWorkflowServiceImpl(ApprovalRequestRepository approvalRequestRepository, ApprovalRoleRepository approvalRoleRepository) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.approvalRoleRepository = approvalRoleRepository;
    }

    // STORY: FPMAPP-8914 - Submit approval request and assign initial approver role based on thresholds
    @Override
    @Transactional
    public ApprovalRequest submitApprovalRequest(ApprovalRequest request) {
        // Determine initial approver role based on request value and configured thresholds
        String initialRole = determineApproverRole(request.getRequestValue());
        request.setCurrentApproverRole(initialRole);
        request.setStatus("PENDING");
        return approvalRequestRepository.save(request);
    }

    // STORY: FPMAPP-8914 - Approve request enforcing role hierarchy and thresholds
    @Override
    @Transactional
    public Optional<ApprovalRequest> approveRequest(Long requestId, String approverRole, String approverName, String comments) throws IllegalAccessException {
        Optional<ApprovalRequest> optRequest = approvalRequestRepository.findById(requestId);
        if (optRequest.isEmpty()) {
            return Optional.empty();
        }
        ApprovalRequest request = optRequest.get();

        // Validate approver role matches current approver role
        if (!request.getCurrentApproverRole().equalsIgnoreCase(approverRole)) {
            throw new IllegalAccessException("Approval request cannot be approved by role: " + approverRole + ". Current approver role: " + request.getCurrentApproverRole());
        }

        // Validate approver role threshold
        ApprovalRole role = approvalRoleRepository.findByRoleName(approverRole)
                .orElseThrow(() -> new IllegalAccessException("Approver role configuration not found: " + approverRole));

        if (request.getRequestValue() > role.getApprovalThreshold()) {
            throw new IllegalAccessException("Request value exceeds approval threshold for role: " + approverRole);
        }

        // If current approver is not the highest role and request value exceeds current role threshold, escalate
        if (needsEscalation(request.getRequestValue(), approverRole)) {
            String nextRole = getNextHigherRole(approverRole);
            if (nextRole == null) {
                // No higher role, approve
                completeApproval(request, approverName, comments);
            } else {
                // Escalate to next role
                request.setCurrentApproverRole(nextRole);
                request.setComments(combineComments(request.getComments(), approverName + " escalated approval to " + nextRole + "."));
            }
        } else {
            // Approve at current level
            completeApproval(request, approverName, comments);
        }

        approvalRequestRepository.save(request);
        return Optional.of(request);
    }

    // STORY: FPMAPP-8914 - Reject request enforcing role hierarchy
    @Override
    @Transactional
    public Optional<ApprovalRequest> rejectRequest(Long requestId, String approverRole, String approverName, String comments) throws IllegalAccessException {
        Optional<ApprovalRequest> optRequest = approvalRequestRepository.findById(requestId);
        if (optRequest.isEmpty()) {
            return Optional.empty();
        }
        ApprovalRequest request = optRequest.get();

        // Validate approver role matches current approver role
        if (!request.getCurrentApproverRole().equalsIgnoreCase(approverRole)) {
            throw new IllegalAccessException("Approval request cannot be rejected by role: " + approverRole + ". Current approver role: " + request.getCurrentApproverRole());
        }

        request.setStatus("REJECTED");
        request.setApprovedBy(approverName);
        request.setApprovedAt(LocalDateTime.now());
        request.setComments(combineComments(request.getComments(), comments));

        approvalRequestRepository.save(request);
        return Optional.of(request);
    }

    // STORY: FPMAPP-8914 - Retrieve approval request by id
    @Override
    public Optional<ApprovalRequest> getApprovalRequest(Long requestId) {
        return approvalRequestRepository.findById(requestId);
    }

    // Helper methods

    private String determineApproverRole(Double requestValue) {
        // STORY: FPMAPP-8914 - Determine initial approver role based on thresholds
        // TODO: Consider caching roles and thresholds for performance
        List<ApprovalRole> roles = approvalRoleRepository.findAll();
        // Sort roles by threshold ascending
        roles.sort(Comparator.comparingDouble(ApprovalRole::getApprovalThreshold));

        for (ApprovalRole role : roles) {
            if (requestValue <= role.getApprovalThreshold()) {
                return role.getRoleName();
            }
        }
        // If exceeds all thresholds, assign highest role
        return roles.stream()
                .max(Comparator.comparingDouble(ApprovalRole::getApprovalThreshold))
                .map(ApprovalRole::getRoleName)
                .orElse("DIRECTOR"); // default fallback
    }

    private boolean needsEscalation(Double requestValue, String currentRole) throws IllegalAccessException {
        ApprovalRole role = approvalRoleRepository.findByRoleName(currentRole)
                .orElseThrow(() -> new IllegalAccessException("Approver role configuration not found: " + currentRole));
        return requestValue > role.getApprovalThreshold();
    }

    private String getNextHigherRole(String currentRole) {
        int idx = ROLE_HIERARCHY.indexOf(currentRole.toUpperCase());
        if (idx < 0 || idx + 1 >= ROLE_HIERARCHY.size()) {
            return null;
        }
        return ROLE_HIERARCHY.get(idx + 1);
    }

    private void completeApproval(ApprovalRequest request, String approverName, String comments) {
        request.setStatus("APPROVED");
        request.setApprovedBy(approverName);
        request.setApprovedAt(LocalDateTime.now());
        request.setComments(combineComments(request.getComments(), comments));
    }

    private String combineComments(String existing, String addition) {
        if (existing == null || existing.isBlank()) {
            return addition;
        }
        return existing + "\n" + addition;
    }

}
