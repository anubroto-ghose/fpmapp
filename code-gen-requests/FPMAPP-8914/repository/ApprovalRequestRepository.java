package com.fpm.repository;

import com.fpm.model.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

    // STORY: FPMAPP-8914 - Repository to manage approval requests

    List<ApprovalRequest> findByCurrentApproverRoleAndStatus(String currentApproverRole, String status);

}
