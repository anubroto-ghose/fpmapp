package com.fpm.service.impl;

import com.fpm.dto.DelegationRequestDTO;
import com.fpm.exception.AuthorizationException;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.Delegation;
import com.fpm.model.User;
import com.fpm.repository.ApprovalRequestRepository;
import com.fpm.repository.DelegationRepository;
import com.fpm.repository.UserRepository;
import com.fpm.service.ApprovalService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalServiceImpl.class);

    private final DelegationRepository delegationRepository;
    private final UserRepository userRepository;
    private final ApprovalRequestRepository approvalRequestRepository;

    public ApprovalServiceImpl(DelegationRepository delegationRepository, UserRepository userRepository, ApprovalRequestRepository approvalRequestRepository) {
        this.delegationRepository = delegationRepository;
        this.userRepository = userRepository;
        this.approvalRequestRepository = approvalRequestRepository;
    }

    // STORY: FPMAPP-8910 - Create a delegation of approval authority with validation and audit logging
    @Transactional
    @Override
    public Delegation createDelegation(DelegationRequestDTO request) {
        User approver = userRepository.findById(request.getApproverId())
                .orElseThrow(() -> new IllegalArgumentException("Approver user not found"));
        User delegate = userRepository.findById(request.getDelegateId())
                .orElseThrow(() -> new IllegalArgumentException("Delegate user not found"));

        // Validate roles and permissions
        if (!approver.hasRole("APPROVER")) {
            throw new AuthorizationException("User is not an approver and cannot delegate approval authority");
        }

        if (!delegate.isAuthorizedForDelegation()) {
            throw new AuthorizationException("Delegate user is not authorized to receive delegation");
        }

        // TODO: Validate permissions string format and content against business rules

        Delegation delegation = new Delegation();
        delegation.setApprover(approver);
        delegation.setDelegate(delegate);
        delegation.setStartTime(request.getStartTime());
        delegation.setEndTime(request.getEndTime());
        delegation.setPermissions(request.getPermissions());

        Delegation saved = delegationRepository.save(delegation);

        logger.info("Delegation created: Approver {} delegated to {} from {} to {} with permissions {}",
                approver.getUsername(), delegate.getUsername(), request.getStartTime(), request.getEndTime(), request.getPermissions());

        return saved;
    }

    // STORY: FPMAPP-8910 - Check if a user has delegated approval rights for a given approval request
    @Override
    public boolean isUserDelegateForApproval(User user, ApprovalRequest approvalRequest) {
        LocalDateTime now = LocalDateTime.now();
        List<Delegation> delegations = delegationRepository.findByApproverAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                approvalRequest.getApprover(), now, now);

        for (Delegation delegation : delegations) {
            if (delegation.getDelegate().equals(user)) {
                // TODO: Check if delegation permissions allow this approval action
                return true;
            }
        }
        return false;
    }

    // STORY: FPMAPP-8910 - Mark approval request as delegated and log delegation info
    @Transactional
    @Override
    public void markApprovalRequestDelegated(ApprovalRequest approvalRequest, Delegation delegation) {
        approvalRequest.setDelegated(true); // Assuming ApprovalRequest has a delegated flag
        approvalRequest.setDelegateUser(delegation.getDelegate()); // Assuming ApprovalRequest tracks delegate user
        approvalRequestRepository.save(approvalRequest);

        logger.info("ApprovalRequest {} marked as delegated to {} by approver {}",
                approvalRequest.getId(), delegation.getDelegate().getUsername(), delegation.getApprover().getUsername());
    }

    // Additional methods for audit trail and delegation history can be added here

}
