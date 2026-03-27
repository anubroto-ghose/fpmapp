package com.fpm.service.impl;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.service.CurrencyOverrideService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyOverrideServiceImpl implements CurrencyOverrideService {

    // STORY: FPMAPP-8756 - Check if user has permission to submit currency override
    @Override
    public boolean hasOverridePermission(String username) {
        // TODO: Implement actual permission check logic, e.g., query user roles/authorities
        // For now, simulate that only users with 'ADMIN_OVERRIDE' role have permission
        // This is a stub implementation
        return false;
    }

    // STORY: FPMAPP-8756 - Apply currency override if authorized
    @Override
    public void applyOverride(CurrencyOverrideRequest request, String username) {
        // TODO: Implement override application logic
        // This method should not be called if user is unauthorized
    }
}