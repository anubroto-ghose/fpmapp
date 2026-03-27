package com.fpm.repository;

import com.fpm.model.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogEntryRepository extends JpaRepository<AuditLogEntry, Long> {
    // STORY: FPMAPP-8770 - Repository for persisting immutable audit log entries
}
