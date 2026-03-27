package com.fpm.service;

import com.fpm.dto.CurrencyOverrideRequest;

public interface CurrencyOverrideService {

    // STORY: FPMAPP-8757 - Validate currency pair for override submission
    boolean isValidCurrencyPair(String fromCurrency, String toCurrency);

    // STORY: FPMAPP-8757 - Apply currency override if valid and authorized
    boolean applyOverride(CurrencyOverrideRequest request);
}