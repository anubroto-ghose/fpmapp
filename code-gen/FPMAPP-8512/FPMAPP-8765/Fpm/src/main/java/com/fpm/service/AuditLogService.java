package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8765 - Service interface to log audit entries
    void logRejectionAction(String userId, String comments);
}