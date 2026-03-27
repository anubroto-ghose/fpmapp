package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8768 - Service interface for audit log operations
    void logApprovalAction(String userId, String comments);
}