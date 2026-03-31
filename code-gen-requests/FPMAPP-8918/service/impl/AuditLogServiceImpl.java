package com.fpm.service.impl;

import com.fpm.model.AuditLog;
import com.fpm.repository.AuditLogRepository;
import com.fpm.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8918 - log approval or rejection decision
    public void logApprovalDecision(Long requestId, Long userId, String decisionDetails) {
        AuditLog log = new AuditLog(requestId, userId, "APPROVAL_DECISION", decisionDetails, LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8918 - log delegation action or override
    public void logDelegationAction(Long requestId, Long userId, String delegationDetails) {
        AuditLog log = new AuditLog(requestId, userId, "DELEGATION_ACTION", delegationDetails, LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8918 - log request changes
    public void logRequestChange(Long requestId, Long userId, String changeDetails) {
        AuditLog log = new AuditLog(requestId, userId, "REQUEST_CHANGE", changeDetails, LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Override
    // STORY: FPMAPP-8918 - get audit logs by request
    public List<AuditLog> getAuditLogsByRequest(Long requestId) {
        return auditLogRepository.findByRequestId(requestId);
    }

    @Override
    // STORY: FPMAPP-8918 - get audit logs by user
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Override
    // STORY: FPMAPP-8918 - get audit logs by date range
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    @Override
    // STORY: FPMAPP-8918 - get audit logs by request and user
    public List<AuditLog> getAuditLogsByRequestAndUser(Long requestId, Long userId) {
        return auditLogRepository.findByRequestIdAndUserId(requestId, userId);
    }

    @Override
    // STORY: FPMAPP-8918 - get audit logs by request and date range
    public List<AuditLog> getAuditLogsByRequestAndDateRange(Long requestId, LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByRequestIdAndTimestampBetween(requestId, start, end);
    }
}
