package com.fpm.repository;

import com.fpm.model.ApprovalDelegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalDelegationRepository extends JpaRepository<ApprovalDelegation, Long> {

    List<ApprovalDelegation> findByDelegatorUserIdAndActiveTrue(Long delegatorUserId);

    List<ApprovalDelegation> findByDelegateeUserIdAndActiveTrue(Long delegateeUserId);

    Optional<ApprovalDelegation> findByIdAndActiveTrue(Long id);

    // STORY: FPMAPP-8915 - Repository for managing approval delegations
}
