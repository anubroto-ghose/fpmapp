package com.fpm.service.impl;

import com.fpm.model.ApprovalDelegation;
import com.fpm.model.AuditLog;
import com.fpm.repository.ApprovalDelegationRepository;
import com.fpm.repository.AuditLogRepository;
import com.fpm.service.ApprovalDelegationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApprovalDelegationServiceImpl implements ApprovalDelegationService {

    private final ApprovalDelegationRepository delegationRepository;
    private final AuditLogRepository auditLogRepository;

    public ApprovalDelegationServiceImpl(ApprovalDelegationRepository delegationRepository, AuditLogRepository auditLogRepository) {
        this.delegationRepository = delegationRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public ApprovalDelegation createDelegation(ApprovalDelegation delegation) {
        delegation.setActive(true);
        delegation.setCreatedAt(LocalDateTime.now());
        ApprovalDelegation saved = delegationRepository.save(delegation);

        // STORY: FPMAPP-8915 - Log delegation creation event
        AuditLog log = new AuditLog();
        log.setEventType("DELEGATION_CREATED");
        log.setUserId(delegation.getDelegatorUserId());
        log.setRelatedEntityId(saved.getId());
        log.setEventDescription(String.format("Delegation created from user %d to user %d with permissions: %s",
                delegation.getDelegatorUserId(), delegation.getDelegateeUserId(), delegation.getPermissions()));
        auditLogRepository.save(log);

        return saved;
    }

    @Override
    public Optional<ApprovalDelegation> getDelegationById(Long id) {
        return delegationRepository.findByIdAndActiveTrue(id);
    }

    @Override
    public List<ApprovalDelegation> getActiveDelegationsByDelegator(Long delegatorUserId) {
        return delegationRepository.findByDelegatorUserIdAndActiveTrue(delegatorUserId);
    }

    @Override
    public List<ApprovalDelegation> getActiveDelegationsByDelegatee(Long delegateeUserId) {
        return delegationRepository.findByDelegateeUserIdAndActiveTrue(delegateeUserId);
    }

    @Override
    @Transactional
    public ApprovalDelegation updateDelegation(ApprovalDelegation delegation) {
        // STORY: FPMAPP-8915 - Update delegation and log the modification
        Optional<ApprovalDelegation> existingOpt = delegationRepository.findByIdAndActiveTrue(delegation.getId());
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Delegation not found or inactive");
        }
        ApprovalDelegation existing = existingOpt.get();
        existing.setPermissions(delegation.getPermissions());
        existing.setStartTime(delegation.getStartTime());
        existing.setEndTime(delegation.getEndTime());
        existing.setUpdatedAt(LocalDateTime.now());
        ApprovalDelegation updated = delegationRepository.save(existing);

        AuditLog log = new AuditLog();
        log.setEventType("DELEGATION_UPDATED");
        log.setUserId(existing.getDelegatorUserId());
        log.setRelatedEntityId(updated.getId());
        log.setEventDescription(String.format("Delegation updated from user %d to user %d with permissions: %s",
                updated.getDelegatorUserId(), updated.getDelegateeUserId(), updated.getPermissions()));
        auditLogRepository.save(log);

        return updated;
    }

    @Override
    @Transactional
    public void revokeDelegation(Long delegationId, Long revokedByUserId) {
        // STORY: FPMAPP-8915 - Revoke delegation and log the revocation
        Optional<ApprovalDelegation> existingOpt = delegationRepository.findByIdAndActiveTrue(delegationId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Delegation not found or already inactive");
        }
        ApprovalDelegation delegation = existingOpt.get();
        delegation.setActive(false);
        delegation.setUpdatedAt(LocalDateTime.now());
        delegationRepository.save(delegation);

        AuditLog log = new AuditLog();
        log.setEventType("DELEGATION_REVOKED");
        log.setUserId(revokedByUserId);
        log.setRelatedEntityId(delegation.getId());
        log.setEventDescription(String.format("Delegation revoked by user %d from delegator %d to delegatee %d",
                revokedByUserId, delegation.getDelegatorUserId(), delegation.getDelegateeUserId()));
        auditLogRepository.save(log);
    }

    @Override
    public boolean isUserDelegateeWithPermission(Long delegateeUserId, String permission) {
        // STORY: FPMAPP-8915 - Check if user is active delegatee with given permission
        List<ApprovalDelegation> delegations = delegationRepository.findByDelegateeUserIdAndActiveTrue(delegateeUserId);
        for (ApprovalDelegation delegation : delegations) {
            // TODO: parse permissions string and check if permission is included
            // For simplicity, assume permissions is a CSV string
            String[] perms = delegation.getPermissions().split(",");
            for (String perm : perms) {
                if (perm.trim().equalsIgnoreCase(permission)) {
                    // Also check if current time is within start and end time
                    LocalDateTime now = LocalDateTime.now();
                    if ((delegation.getStartTime() == null || !now.isBefore(delegation.getStartTime())) &&
                        (delegation.getEndTime() == null || !now.isAfter(delegation.getEndTime()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
