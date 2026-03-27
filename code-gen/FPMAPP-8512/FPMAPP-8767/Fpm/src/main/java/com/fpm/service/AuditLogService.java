package com.fpm.service;

import com.fpm.model.AuditLog;

public interface AuditLogService {

    // STORY: FPMAPP-8767 - Record rejection action audit log with optional comments
    void recordRejectionAction(String userId, String comments);

}