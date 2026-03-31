package com.fpm.repository;

import com.fpm.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // STORY: FPMAPP-8911 - Repository to query audit logs by approval request
    List<AuditLog> findByApprovalRequestIdOrderByActionTimestampAsc(Long approvalRequestId);
}
