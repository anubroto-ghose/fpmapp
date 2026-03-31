package com.fpm.service;

import com.fpm.model.AuditTrail;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditTrailService {

    // STORY: FPMAPP-9045 - Persist audit trail entry
    void logAction(String requestId, String userId, String actionType, String actionDetails, String delegatedToRole);

    // STORY: FPMAPP-9045 - Retrieve audit logs filtered by requestId and date range
    List<AuditTrail> getAuditLogs(String requestId, LocalDateTime startDate, LocalDateTime endDate);

}
