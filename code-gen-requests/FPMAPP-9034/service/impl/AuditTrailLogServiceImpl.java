package com.fpm.service.impl;

import com.fpm.model.AuditTrailLog;
import com.fpm.repository.AuditTrailLogRepository;
import com.fpm.service.AuditTrailLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditTrailLogServiceImpl implements AuditTrailLogService {

    private final AuditTrailLogRepository auditTrailLogRepository;

    public AuditTrailLogServiceImpl(AuditTrailLogRepository auditTrailLogRepository) {
        this.auditTrailLogRepository = auditTrailLogRepository;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-9034 - Persist immutable audit trail log
    public AuditTrailLog logAuditTrail(AuditTrailLog auditTrailLog) {
        // TODO: Ensure immutability by never updating existing records
        return auditTrailLogRepository.save(auditTrailLog);
    }

    @Override
    // STORY: FPMAPP-9034 - Retrieve audit trail logs by entity
    public List<AuditTrailLog> getAuditTrailByEntity(String entityType, Long entityId) {
        return auditTrailLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    @Override
    // STORY: FPMAPP-9034 - Retrieve audit trail logs by action type
    public List<AuditTrailLog> getAuditTrailByActionType(AuditTrailLog.ActionType actionType) {
        return auditTrailLogRepository.findByActionTypeOrderByTimestampDesc(actionType);
    }
}
