package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8770 - Service interface to log audit entries
    void logApprovalAction(String userId, String details);
}
