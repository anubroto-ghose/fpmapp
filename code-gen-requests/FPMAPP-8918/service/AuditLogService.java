package com.fpm.service;

import com.fpm.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogService {

    // STORY: FPMAPP-8918 - log an approval or rejection decision
    void logApprovalDecision(Long requestId, Long userId, String decisionDetails);

    // STORY: FPMAPP-8918 - log a delegation action or override
    void logDelegationAction(Long requestId, Long userId, String delegationDetails);

    // STORY: FPMAPP-8918 - log a request change with details
    void logRequestChange(Long requestId, Long userId, String changeDetails);

    // STORY: FPMAPP-8918 - query audit logs by request id
    List<AuditLog> getAuditLogsByRequest(Long requestId);

    // STORY: FPMAPP-8918 - query audit logs by user id
    List<AuditLog> getAuditLogsByUser(Long userId);

    // STORY: FPMAPP-8918 - query audit logs by date range
    List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8918 - query audit logs by request and user
    List<AuditLog> getAuditLogsByRequestAndUser(Long requestId, Long userId);

    // STORY: FPMAPP-8918 - query audit logs by request and date range
    List<AuditLog> getAuditLogsByRequestAndDateRange(Long requestId, LocalDateTime start, LocalDateTime end);

}
