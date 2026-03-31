package com.fpm.service;

import com.fpm.model.ApprovalDelegation;
import java.util.List;
import java.util.Optional;

public interface ApprovalDelegationService {

    // STORY: FPMAPP-8915 - Service interface for approval delegation management

    ApprovalDelegation createDelegation(ApprovalDelegation delegation);

    Optional<ApprovalDelegation> getDelegationById(Long id);

    List<ApprovalDelegation> getActiveDelegationsByDelegator(Long delegatorUserId);

    List<ApprovalDelegation> getActiveDelegationsByDelegatee(Long delegateeUserId);

    ApprovalDelegation updateDelegation(ApprovalDelegation delegation);

    void revokeDelegation(Long delegationId, Long revokedByUserId);

    boolean isUserDelegateeWithPermission(Long delegateeUserId, String permission);

}
