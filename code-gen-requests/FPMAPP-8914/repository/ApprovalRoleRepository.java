package com.fpm.repository;

import com.fpm.model.ApprovalRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApprovalRoleRepository extends JpaRepository<ApprovalRole, Long> {

    // STORY: FPMAPP-8914 - Repository to fetch approval role and thresholds

    Optional<ApprovalRole> findByRoleName(String roleName);

}
