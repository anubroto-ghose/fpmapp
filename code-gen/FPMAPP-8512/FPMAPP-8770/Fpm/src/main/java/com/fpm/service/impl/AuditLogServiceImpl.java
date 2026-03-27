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
    // STORY: FPMAPP-8770 - Log approval action with current system time and user details
    public void logApprovalAction(String userId, String details) {
        Instant currentTimestamp = Instant.now(); // Use system time at moment of approval
        AuditLogEntry entry = new AuditLogEntry(userId, "Approval", currentTimestamp, details);
        auditLogEntryRepository.save(entry);
    }
}
