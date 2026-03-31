package com.fpm.service;

import com.fpm.dto.ApprovalDelegationDto;
import com.fpm.model.ApprovalDelegation;
import java.util.List;

public interface ApprovalDelegationService {

    // STORY: FPMAPP-8915 - Delegate approval rights to another user with permissions
    ApprovalDelegation delegateApproval(Long delegatorUserId, ApprovalDelegationDto delegationDto);

    // STORY: FPMAPP-8915 - Revoke or modify delegation permissions
    ApprovalDelegation updateDelegation(Long delegatorUserId, Long delegationId, ApprovalDelegationDto delegationDto);

    // STORY: FPMAPP-8915 - Revoke delegation by marking inactive
    void revokeDelegation(Long delegatorUserId, Long delegationId);

    // STORY: FPMAPP-8915 - Retrieve active delegations for a user
    List<ApprovalDelegation> getActiveDelegationsForUser(Long userId);

}
