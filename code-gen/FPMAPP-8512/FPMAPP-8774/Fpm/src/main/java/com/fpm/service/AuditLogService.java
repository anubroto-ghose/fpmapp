package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8774 - Service interface to handle audit log operations
    void recordApprovalAction(String userId, String comments);
}