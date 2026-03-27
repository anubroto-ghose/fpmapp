package com.fpm.service;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.exception.PermissionDeniedException;

public interface CurrencyConversionService {

    // STORY: FPMAPP-8758 - Check if user has override permission
    boolean hasOverridePermission(String username);

    // STORY: FPMAPP-8758 - Apply currency override if authorized and valid
    void applyOverride(CurrencyOverrideRequest request, String username) throws PermissionDeniedException;
}