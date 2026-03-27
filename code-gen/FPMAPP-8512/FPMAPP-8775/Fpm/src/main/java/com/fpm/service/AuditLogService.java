package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8775 - Service interface to log audit actions
    void logRejectionAction(String userId, String comments);
}