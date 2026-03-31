package com.fpm.repository;

import com.fpm.model.DelegationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DelegationLogRepository extends JpaRepository<DelegationLog, Long> {
    // STORY: FPMAPP-9047 - Repository for delegation_logs table
}
