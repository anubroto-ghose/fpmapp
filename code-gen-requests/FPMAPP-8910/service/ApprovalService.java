package com.fpm.service;

import com.fpm.dto.DelegationRequestDTO;
import com.fpm.exception.AuthorizationException;
import com.fpm.model.ApprovalRequest;
import com.fpm.model.User;

import java.util.List;

public interface ApprovalService {

    // STORY: FPMAPP-8910 - Delegate approval authority to another user
    void delegateApprovalAuthority(User approver, DelegationRequestDTO delegationRequest) throws AuthorizationException;

    // STORY: FPMAPP-8910 - Check if a user has delegated approval rights for a given approval request
    boolean isUserDelegateForApproval(User user, ApprovalRequest approvalRequest);

    // STORY: FPMAPP-8910 - Retrieve delegation history for an approval request
    List<String> getDelegationAuditTrail(Long approvalRequestId);

}
