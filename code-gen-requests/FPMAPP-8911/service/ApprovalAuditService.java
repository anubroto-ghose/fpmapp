package com.fpm.service;

import com.fpm.model.AuditLog;

import java.util.List;

public interface ApprovalAuditService {

    // STORY: FPMAPP-8911 - Service interface to handle audit log creation and retrieval
    void logApprovalAction(Long approvalRequestId, String userId, String actionType, String comments);

    List<AuditLog> getAuditLogsForApprovalRequest(Long approvalRequestId);
}
