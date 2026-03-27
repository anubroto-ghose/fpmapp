package com.fpm.service;

import com.fpm.model.AuditLogEntry;
import com.fpm.repository.AuditLogEntryRepository;
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
    // STORY: FPMAPP-8768 - Log approval action with user details and timestamp
    public void logApprovalAction(String userId, String comments) {
        Instant now = Instant.now(); // Accurate system timestamp
        AuditLogEntry entry = new AuditLogEntry(userId, "Approval", now, comments);
        auditLogEntryRepository.save(entry);
        // TODO: Consider publishing event or notification if needed
    }
}