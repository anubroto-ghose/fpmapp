package com.fpm.repository;

import com.fpm.model.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    // STORY: FPMAPP-9045 - Retrieve audit logs filtered by requestId and date range
    List<AuditTrail> findByRequestIdAndTimestampBetween(String requestId, LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-9045 - Retrieve audit logs filtered by requestId only
    List<AuditTrail> findByRequestId(String requestId);

}
