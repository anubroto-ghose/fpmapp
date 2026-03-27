package com.fpm.service;

import com.fpm.model.AuditLogEntry;

public interface AuditLogService {
    // STORY: FPMAPP-8764 - Service interface to handle audit log operations
    void recordDelegationAction(String delegatorUserId, String comments);
}