package com.fpm.repository;

import com.fpm.model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    Optional<Approval> findByApprovalId(String approvalId);
}
