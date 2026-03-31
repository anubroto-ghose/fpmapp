package com.fpm.repository;

import com.fpm.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // STORY: FPMAPP-8915 - Repository for audit log persistence
}
