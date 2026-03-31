package com.fpm.service.impl;

import com.fpm.dto.ApprovalRequestDTO.DelegationInfo;
import com.fpm.service.DelegationService;
import org.springframework.stereotype.Service;

// STORY: FPMAPP-9038 - Service implementation to enforce delegation rules
@Service
public class DelegationServiceImpl implements DelegationService {

    @Override
    public boolean isDelegationAllowed(String userRole, DelegationInfo delegationInfo) {
        // TODO: Implement delegation rules enforcement logic based on userRole and delegationInfo
        // Example: Only roles "Manager" and "TeamLead" can delegate
        if (userRole == null || delegationInfo == null) {
            return false;
        }
        if ("Manager".equalsIgnoreCase(userRole) || "TeamLead".equalsIgnoreCase(userRole)) {
            return true;
        }
        return false;
    }
}