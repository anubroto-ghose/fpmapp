package com.fpm.repository;

import com.fpm.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // STORY: FPMAPP-8918 - Query audit logs by requestId
    List<AuditLog> findByRequestIdOrderByTimestampDesc(Long requestId);

    // STORY: FPMAPP-8918 - Query audit logs by userId
    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);

    // STORY: FPMAPP-8918 - Query audit logs by timestamp range
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8918 - Query audit logs by requestId and userId
    List<AuditLog> findByRequestIdAndUserIdOrderByTimestampDesc(Long requestId, String userId);

}
