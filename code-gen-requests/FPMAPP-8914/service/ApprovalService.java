package com.fpm.service;

import com.fpm.model.ApprovalRequest;
import java.util.Optional;

public interface ApprovalService {

    // STORY: FPMAPP-8914 - Service interface for hierarchical role-based approval workflow

    ApprovalRequest createApprovalRequest(Double requestValue, String requestedBy);

    Optional<ApprovalRequest> getApprovalRequest(Long id);

    ApprovalRequest approveRequest(Long requestId, String approverRole) throws IllegalAccessException;

    ApprovalRequest rejectRequest(Long requestId, String approverRole) throws IllegalAccessException;

}
