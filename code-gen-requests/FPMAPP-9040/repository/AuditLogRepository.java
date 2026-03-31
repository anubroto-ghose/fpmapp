package com.fpm.repository;

import com.fpm.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
    // STORY: FPMAPP-9040 - Repository for AuditLog with support for dynamic filtering via Specifications
}
