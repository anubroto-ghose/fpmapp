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
    // STORY: FPMAPP-8765 - Log rejection action with user details, timestamp, action type, and comments
    public void logRejectionAction(String userId, String comments) {
        Instant now = Instant.now(); // Use system time for timestamp
        AuditLogEntry entry = new AuditLogEntry(userId, now, "Rejection", comments);
        auditLogEntryRepository.save(entry);
        // TODO: Consider publishing event or notification if needed
    }
}