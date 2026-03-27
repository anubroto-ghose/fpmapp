package com.fpm.service;

import com.fpm.dto.CurrencyOverrideRequest;

public interface CurrencyOverrideService {

    // STORY: FPMAPP-8756 - Check if user has permission to submit currency override
    boolean hasOverridePermission(String username);

    // STORY: FPMAPP-8756 - Apply currency override if authorized
    void applyOverride(CurrencyOverrideRequest request, String username);
}