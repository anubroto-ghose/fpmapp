package com.fpm.service.impl;

import com.fpm.model.AuditLog;
import com.fpm.repository.AuditLogRepository;
import com.fpm.service.AuditLogService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // STORY: FPMAPP-9040 - Persist audit log entry ensuring immutability by not allowing updates
    @Override
    public void saveAuditLog(AuditLog auditLog) {
        // TODO: Consider async saving or batching if performance impact is observed
        auditLogRepository.save(auditLog);
    }

    // STORY: FPMAPP-9040 - Retrieve audit logs filtered by optional parameters
    @Override
    public List<AuditLog> getAuditLogs(String actionType, String userId, LocalDateTime fromDate, LocalDateTime toDate) {
        Specification<AuditLog> spec = buildSpecification(actionType, userId, fromDate, toDate);
        return auditLogRepository.findAll(spec);
    }

    // STORY: FPMAPP-9040 - Retrieve audit logs with pagination and filtering
    @Override
    public Page<AuditLog> getAuditLogsPaged(String actionType, String userId, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Specification<AuditLog> spec = buildSpecification(actionType, userId, fromDate, toDate);
        return auditLogRepository.findAll(spec, pageable);
    }

    private Specification<AuditLog> buildSpecification(String actionType, String userId, LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(actionType)) {
                predicates.add(cb.equal(root.get("actionType"), actionType));
            }
            if (StringUtils.hasText(userId)) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), toDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
