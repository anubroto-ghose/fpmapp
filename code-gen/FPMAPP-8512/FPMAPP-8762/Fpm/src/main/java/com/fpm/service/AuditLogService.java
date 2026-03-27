package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8762 - Service interface to record audit log entries
    void recordDelegationAction(String delegatorUserId, String comments);
}
