package com.fpm.service;

import com.fpm.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    // STORY: FPMAPP-8918 - Save a new audit log entry
    AuditLog logAuditEvent(AuditLog auditLog);

    // STORY: FPMAPP-8918 - Retrieve audit logs by requestId
    List<AuditLog> getAuditLogsByRequestId(Long requestId);

    // STORY: FPMAPP-8918 - Retrieve audit logs by userId
    List<AuditLog> getAuditLogsByUserId(String userId);

    // STORY: FPMAPP-8918 - Retrieve audit logs by date range
    List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8918 - Retrieve audit logs by requestId and userId
    List<AuditLog> getAuditLogsByRequestIdAndUserId(Long requestId, String userId);

}
