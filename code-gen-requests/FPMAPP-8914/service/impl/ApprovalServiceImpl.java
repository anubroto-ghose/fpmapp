package com.fpm.service.impl;

import com.fpm.model.ApprovalRequest;
import com.fpm.model.ApprovalRole;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.ApprovalRoleRepository;
import com.fpm.service.ApprovalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalRoleRepository approvalRoleRepository;

    public ApprovalServiceImpl(ApprovalRequestRepository approvalRequestRepository, ApprovalRoleRepository approvalRoleRepository) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.approvalRoleRepository = approvalRoleRepository;
    }

    // STORY: FPMAPP-8914 - Create approval request and assign initial approver role based on thresholds
    @Override
    @Transactional
    public ApprovalRequest createApprovalRequest(Double requestValue, String requestedBy) {
        // Fetch all roles sorted by threshold ascending
        List<ApprovalRole> roles = approvalRoleRepository.findAll();
        roles.sort(Comparator.comparingDouble(ApprovalRole::getApprovalThreshold));

        String assignedRole = null;
        for (ApprovalRole role : roles) {
            if (requestValue <= role.getApprovalThreshold()) {
                assignedRole = role.getRoleName();
                break;
            }
        }

        if (assignedRole == null) {
            // TODO: Decide what to do if requestValue exceeds all thresholds (e.g. require director or special approval)
            // For now assign highest role
            assignedRole = roles.stream()
                    .max(Comparator.comparingDouble(ApprovalRole::getApprovalThreshold))
                    .map(ApprovalRole::getRoleName)
                    .orElse("DIRECTOR");
        }

        ApprovalRequest request = new ApprovalRequest(requestValue, "PENDING", requestedBy, assignedRole);
        return approvalRequestRepository.save(request);
    }

    // STORY: FPMAPP-8914 - Retrieve approval request by id
    @Override
    public Optional<ApprovalRequest> getApprovalRequest(Long id) {
        return approvalRequestRepository.findById(id);
    }

    // STORY: FPMAPP-8914 - Approve request enforcing role hierarchy and thresholds
    @Override
    @Transactional
    public ApprovalRequest approveRequest(Long requestId, String approverRole) throws IllegalAccessException {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found"));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Request is not pending approval");
        }

        // Validate approver role can approve this request
        ApprovalRole approverRoleEntity = approvalRoleRepository.findByRoleName(approverRole)
                .orElseThrow(() -> new IllegalArgumentException("Approver role not found"));

        if (request.getRequestValue() > approverRoleEntity.getApprovalThreshold()) {
            throw new IllegalAccessException("Approver role cannot approve this request value");
        }

        // Check if approver role matches current approver role or is higher in hierarchy
        // TODO: Define role hierarchy order - assuming roles sorted ascending by threshold
        List<ApprovalRole> roles = approvalRoleRepository.findAll();
        roles.sort(Comparator.comparingDouble(ApprovalRole::getApprovalThreshold));

        int currentApproverIndex = findRoleIndex(roles, request.getCurrentApproverRole());
        int approverIndex = findRoleIndex(roles, approverRole);

        if (approverIndex < currentApproverIndex) {
            throw new IllegalAccessException("Approver role cannot bypass the defined role hierarchy");
        }

        // Approve the request
        request.setStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());
        return approvalRequestRepository.save(request);
    }

    // STORY: FPMAPP-8914 - Reject request enforcing role hierarchy
    @Override
    @Transactional
    public ApprovalRequest rejectRequest(Long requestId, String approverRole) throws IllegalAccessException {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Approval request not found"));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new IllegalStateException("Request is not pending approval");
        }

        ApprovalRole approverRoleEntity = approvalRoleRepository.findByRoleName(approverRole)
                .orElseThrow(() -> new IllegalArgumentException("Approver role not found"));

        if (request.getRequestValue() > approverRoleEntity.getApprovalThreshold()) {
            throw new IllegalAccessException("Approver role cannot reject this request value");
        }

        List<ApprovalRole> roles = approvalRoleRepository.findAll();
        roles.sort(Comparator.comparingDouble(ApprovalRole::getApprovalThreshold));

        int currentApproverIndex = findRoleIndex(roles, request.getCurrentApproverRole());
        int approverIndex = findRoleIndex(roles, approverRole);

        if (approverIndex < currentApproverIndex) {
            throw new IllegalAccessException("Approver role cannot bypass the defined role hierarchy");
        }

        request.setStatus("REJECTED");
        request.setUpdatedAt(LocalDateTime.now());
        return approvalRequestRepository.save(request);
    }

    private int findRoleIndex(List<ApprovalRole> roles, String roleName) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getRoleName().equalsIgnoreCase(roleName)) {
                return i;
            }
        }
        return -1; // role not found
    }

}
