package com.fpm.repository;

import com.fpm.model.CurrencyOverrideAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyOverrideAuditLogRepository extends JpaRepository<CurrencyOverrideAuditLog, Long> {

    // STORY: FPMAPP-9043 - Retrieve audit logs ordered by timestamp descending
    List<CurrencyOverrideAuditLog> findAllByOrderByTimestampDesc();
}
