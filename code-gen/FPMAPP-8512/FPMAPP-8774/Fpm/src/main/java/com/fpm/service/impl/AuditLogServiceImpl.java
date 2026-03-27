package com.fpm.service.impl;

import com.fpm.model.AuditLogEntry;
import com.fpm.repository.AuditLogEntryRepository;
import com.fpm.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogEntryRepository auditLogEntryRepository;

    public AuditLogServiceImpl(AuditLogEntryRepository auditLogEntryRepository) {
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8774 - Record approval action with user details, timestamp, and comments
    public void recordApprovalAction(String userId, String comments) {
        Instant now = Instant.now();
        AuditLogEntry entry = new AuditLogEntry(userId, "Approval", now, comments);
        auditLogEntryRepository.save(entry);
        // TODO: Consider publishing event or notification if needed
    }
}