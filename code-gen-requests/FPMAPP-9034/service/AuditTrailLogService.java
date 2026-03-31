package com.fpm.service;

import com.fpm.model.AuditTrailLog;

import java.util.List;

public interface AuditTrailLogService {

    // STORY: FPMAPP-9034 - Service interface for audit trail logging and retrieval

    AuditTrailLog logAuditTrail(AuditTrailLog auditTrailLog);

    List<AuditTrailLog> getAuditTrailByEntity(String entityType, Long entityId);

    List<AuditTrailLog> getAuditTrailByActionType(AuditTrailLog.ActionType actionType);

}
