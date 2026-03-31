package com.fpm.service;

import com.fpm.model.AuditLog;

import java.util.List;

public interface ApprovalAuditService {

    // STORY: FPMAPP-8911 - Log an approval-related action
    void logAction(Long approvalRequestId, String userId, String actionType, String comments);

    // STORY: FPMAPP-8911 - Retrieve audit logs for an approval request
    List<AuditLog> getAuditLogsForApprovalRequest(Long approvalRequestId);
}
