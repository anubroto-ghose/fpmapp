package com.fpm.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpm.model.AuditLog;
import com.fpm.repository.AuditLogRepository;
import com.fpm.service.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // STORY: FPMAPP-8767 - Record rejection action audit log with optional comments
    @Override
    public void recordRejectionAction(String userId, String comments) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setActionType("Rejection");
        auditLog.setTimestamp(Instant.now()); // Use system time
        if (comments == null || comments.trim().isEmpty()) {
            auditLog.setComments("No comments provided");
        } else {
            auditLog.setComments(comments.trim());
        }
        auditLog.setImmutable(true); // Mark audit entry as immutable

        // TODO: Ensure immutability is enforced at DB and application level

        auditLogRepository.save(auditLog);
    }
}