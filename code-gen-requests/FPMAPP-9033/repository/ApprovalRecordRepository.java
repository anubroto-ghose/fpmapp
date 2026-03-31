package com.fpm.repository;

import com.fpm.model.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// STORY: FPMAPP-9033 - Repository for ApprovalRecord entity
@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

}
