package com.fpm.repository;

import com.fpm.model.AuditTrailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditTrailLogRepository extends JpaRepository<AuditTrailLog, Long> {

    // STORY: FPMAPP-9034 - Repository to access audit trail logs

    List<AuditTrailLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    List<AuditTrailLog> findByActionTypeOrderByTimestampDesc(AuditTrailLog.ActionType actionType);

}
