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
    // STORY: FPMAPP-8762 - Record delegation action with user details, timestamp, and comments
    public void recordDelegationAction(String delegatorUserId, String comments) {
        Instant now = Instant.now();
        AuditLogEntry entry = new AuditLogEntry(delegatorUserId, "Delegation", now, comments);
        auditLogEntryRepository.save(entry);
        // TODO: Consider publishing event or notification if needed
    }
}
