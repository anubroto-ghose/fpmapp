package com.fpm.service.impl;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.service.CurrencyOverrideService;
import org.springframework.stereotype.Service;

@Service
public class CurrencyOverrideServiceImpl implements CurrencyOverrideService {

    // STORY: FPMAPP-8757 - Validate currency pair for override submission
    @Override
    public boolean isValidCurrencyPair(String fromCurrency, String toCurrency) {
        // TODO: Implement actual currency pair validation logic, e.g., check against supported currency pairs
        if (fromCurrency == null || toCurrency == null) {
            return false;
        }
        // Example simple validation: currencies must be 3-letter uppercase codes and not equal
        if (fromCurrency.length() != 3 || toCurrency.length() != 3) {
            return false;
        }
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return false;
        }
        // TODO: Add check against a list of supported currencies if available
        return true;
    }

    // STORY: FPMAPP-8757 - Apply currency override if valid and authorized
    @Override
    public boolean applyOverride(CurrencyOverrideRequest request) {
        // TODO: Implement override application logic
        // For this story, assume override is applied successfully if called
        return true;
    }
}