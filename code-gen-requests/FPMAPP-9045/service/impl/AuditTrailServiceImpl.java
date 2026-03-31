package com.fpm.service.impl;

import com.fpm.model.AuditTrail;
import com.fpm.repository.AuditTrailRepository;
import com.fpm.service.AuditTrailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AuditTrailServiceImpl implements AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    public AuditTrailServiceImpl(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    @Override
    @Transactional
    // STORY: FPMAPP-9045 - Persist audit trail entry atomically
    public void logAction(String requestId, String userId, String actionType, String actionDetails, String delegatedToRole) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setRequestId(requestId);
        auditTrail.setUserId(userId);
        auditTrail.setActionType(actionType);
        auditTrail.setActionDetails(actionDetails);
        auditTrail.setDelegatedToRole(delegatedToRole);
        auditTrail.setTimestamp(LocalDateTime.now());
        auditTrailRepository.save(auditTrail);
    }

    @Override
    // STORY: FPMAPP-9045 - Retrieve audit logs filtered by requestId and date range
    public List<AuditTrail> getAuditLogs(String requestId, LocalDateTime startDate, LocalDateTime endDate) {
        if (requestId == null || requestId.isEmpty()) {
            // TODO: Decide if empty requestId means return empty or all logs - here return empty
            return Collections.emptyList();
        }
        if (startDate != null && endDate != null) {
            return auditTrailRepository.findByRequestIdAndTimestampBetween(requestId, startDate, endDate);
        } else {
            return auditTrailRepository.findByRequestId(requestId);
        }
    }
}
