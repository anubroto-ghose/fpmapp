package com.fpm.service;

import com.fpm.model.ApprovalRequest;
import java.util.Optional;

public interface ApprovalWorkflowService {

    // STORY: FPMAPP-8914 - Service interface for hierarchical role-based approval workflow

    ApprovalRequest submitApprovalRequest(ApprovalRequest request);

    Optional<ApprovalRequest> approveRequest(Long requestId, String approverRole, String approverName, String comments) throws IllegalAccessException;

    Optional<ApprovalRequest> rejectRequest(Long requestId, String approverRole, String approverName, String comments) throws IllegalAccessException;

    Optional<ApprovalRequest> getApprovalRequest(Long requestId);

}
