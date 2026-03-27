package com.fpm.service.impl;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.exception.PermissionDeniedException;
import com.fpm.service.CurrencyConversionService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    // STORY: FPMAPP-8758 - Check if user has override permission
    @Override
    public boolean hasOverridePermission(String username) {
        // TODO: Implement actual permission check logic, e.g. query user roles/permissions
        // For now, assume users without "admin_override" role do not have permission
        return false; // Simulate unauthorized user for this story
    }

    // STORY: FPMAPP-8758 - Apply currency override if authorized and valid
    @Override
    public void applyOverride(CurrencyOverrideRequest request, String username) throws PermissionDeniedException {
        if (!hasOverridePermission(username)) {
            throw new PermissionDeniedException("User does not have permission to apply override.");
        }
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Override reason is required.");
        }
        // TODO: Implement override application logic here
        // e.g. save override to DB, update rates cache, etc.
    }
}