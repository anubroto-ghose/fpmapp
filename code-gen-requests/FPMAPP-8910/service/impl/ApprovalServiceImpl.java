package com.fpm.service.impl;

import com.fpm.dto.DelegationRequestDTO;
import com.fpm.exception.AuthorizationException;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.Delegation;
import com.fpm.model.User;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.DelegationRepository;
import com.fpm.service.ApprovalService;
import com.fpm.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    private DelegationRepository delegationRepository;

    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;

    @Autowired
    private AuditLogger auditLogger;

    // STORY: FPMAPP-8910 - Delegate approval authority to another user with validation and audit logging
    @Override
    @Transactional
    public void delegateApprovalAuthority(User approver, DelegationRequestDTO delegationRequest) throws AuthorizationException {
        // Validate approver role
        if (!approver.hasRole("APPROVER")) {
            throw new AuthorizationException("User is not authorized to delegate approvals");
        }

        // TODO: Validate delegate user exists and is authorized to receive delegation
        User delegateUser = delegationRequest.getDelegateUser();
        if (delegateUser == null) {
            throw new AuthorizationException("Delegate user must be specified");
        }

        // TODO: Validate delegation permissions against business rules
        String permissions = delegationRequest.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            throw new AuthorizationException("Delegation permissions must be specified");
        }

        LocalDateTime startDate = delegationRequest.getStartDate();
        LocalDateTime endDate = delegationRequest.getEndDate();
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new AuthorizationException("Invalid delegation period");
        }

        Delegation delegation = new Delegation();
        delegation.setApprover(approver);
        delegation.setDelegateUser(delegateUser);
        delegation.setPermissions(permissions);
        delegation.setStartDate(startDate);
        delegation.setEndDate(endDate);

        delegationRepository.save(delegation);

        // Log delegation action
        auditLogger.log(String.format("Delegation created by approver %s to delegate %s with permissions [%s] from %s to %s",
                approver.getUsername(), delegateUser.getUsername(), permissions, startDate, endDate));
    }

    // STORY: FPMAPP-8910 - Check if user is delegate for approval request
    @Override
    public boolean isUserDelegateForApproval(User user, ApprovalRequest approvalRequest) {
        LocalDateTime now = LocalDateTime.now();
        List<Delegation> delegations = delegationRepository.findByApproverAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                approvalRequest.getApprover(), now, now);

        for (Delegation delegation : delegations) {
            if (delegation.getDelegateUser().getId().equals(user.getId())) {
                // TODO: Check if delegation permissions cover this approval request
                return true;
            }
        }
        return false;
    }

    // STORY: FPMAPP-8910 - Retrieve delegation audit trail for approval request
    @Override
    public List<String> getDelegationAuditTrail(Long approvalRequestId) {
        // TODO: Implement retrieval of delegation history and changes made by delegates
        // For now, return empty list
        return new ArrayList<>();
    }

}
