package com.fpm.repository;

import com.fpm.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // STORY: FPMAPP-8918 - find audit logs by request id
    List<AuditLog> findByRequestId(Long requestId);

    // STORY: FPMAPP-8918 - find audit logs by user id
    List<AuditLog> findByUserId(Long userId);

    // STORY: FPMAPP-8918 - find audit logs by timestamp between
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8918 - find audit logs by request id and user id
    List<AuditLog> findByRequestIdAndUserId(Long requestId, Long userId);

    // STORY: FPMAPP-8918 - find audit logs by request id and timestamp between
    List<AuditLog> findByRequestIdAndTimestampBetween(Long requestId, LocalDateTime start, LocalDateTime end);

}
