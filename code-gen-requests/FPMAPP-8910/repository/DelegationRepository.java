package com.fpm.repository;

import com.fpm.model.Delegation;
import com.fpm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, Long> {

    // STORY: FPMAPP-8910 - Find active delegations for an approver at a given time
    List<Delegation> findByApproverAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(User approver, LocalDateTime now1, LocalDateTime now2);

    // STORY: FPMAPP-8910 - Find active delegations for a delegate at a given time
    List<Delegation> findByDelegateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(User delegate, LocalDateTime now1, LocalDateTime now2);

}
