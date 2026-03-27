package com.fpm.service;

import com.fpm.model.AuditLogEntry;
import com.fpm.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8764 - Record delegation action with no comments entered
    public void recordDelegationAction(String delegatorUserId, String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            comments = "No comments"; // STORY: FPMAPP-8764 - Default comment when none provided
        }
        AuditLogEntry entry = new AuditLogEntry(
                delegatorUserId,
                "Delegation",
                comments,
                LocalDateTime.now() // STORY: FPMAPP-8764 - Use system time for timestamp
        );
        auditLogRepository.save(entry);
        // STORY: FPMAPP-8764 - Audit entry is immutable by design (no setters for id and timestamp)
    }
}