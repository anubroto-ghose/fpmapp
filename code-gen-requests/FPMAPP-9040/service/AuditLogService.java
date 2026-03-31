package com.fpm.service;

import com.fpm.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    // STORY: FPMAPP-9040 - Save an audit log entry
    void saveAuditLog(AuditLog auditLog);

    // STORY: FPMAPP-9040 - Retrieve audit logs filtered by actionType, userId, and date range
    List<AuditLog> getAuditLogs(String actionType, String userId, LocalDateTime fromDate, LocalDateTime toDate);

    // STORY: FPMAPP-9040 - Retrieve audit logs with pagination and filtering
    Page<AuditLog> getAuditLogsPaged(String actionType, String userId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
