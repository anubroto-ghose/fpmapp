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
    // STORY: FPMAPP-8918 - Save a new immutable audit log entry
    public AuditLog logAuditEvent(AuditLog auditLog) {
        // Ensure immutability by not allowing updates after save
        auditLog.setImmutable(true);
        if (auditLog.getTimestamp() == null) {
            auditLog.setTimestamp(LocalDateTime.now());
        }
        return auditLogRepository.save(auditLog);
    }

    @Override
    // STORY: FPMAPP-8918 - Retrieve audit logs by requestId
    public List<AuditLog> getAuditLogsByRequestId(Long requestId) {
        return auditLogRepository.findByRequestIdOrderByTimestampDesc(requestId);
    }

    @Override
    // STORY: FPMAPP-8918 - Retrieve audit logs by userId
    public List<AuditLog> getAuditLogsByUserId(String userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Override
    // STORY: FPMAPP-8918 - Retrieve audit logs by date range
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    @Override
    // STORY: FPMAPP-8918 - Retrieve audit logs by requestId and userId
    public List<AuditLog> getAuditLogsByRequestIdAndUserId(Long requestId, String userId) {
        return auditLogRepository.findByRequestIdAndUserIdOrderByTimestampDesc(requestId, userId);
    }
}
