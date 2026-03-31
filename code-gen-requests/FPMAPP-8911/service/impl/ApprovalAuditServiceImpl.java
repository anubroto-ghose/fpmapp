package com.fpm.service.impl;

import com.fpm.model.AuditLog;
import com.fpm.repository.AuditLogRepository;
import com.fpm.service.ApprovalAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalAuditServiceImpl implements ApprovalAuditService {

    private final AuditLogRepository auditLogRepository;

    public ApprovalAuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-8911 - Log approval, rejection, delegation, override actions immutably
    public void logApprovalAction(Long approvalRequestId, String userId, String actionType, String comments) {
        // TODO: Implement encryption of comments if required
        AuditLog auditLog = new AuditLog(approvalRequestId, userId, actionType, comments, LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    @Override
    // STORY: FPMAPP-8911 - Retrieve audit logs for a given approval request
    public List<AuditLog> getAuditLogsForApprovalRequest(Long approvalRequestId) {
        return auditLogRepository.findByApprovalRequestIdOrderByActionTimestampAsc(approvalRequestId);
    }
}
