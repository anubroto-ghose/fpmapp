package com.fpm.service;

import java.math.BigDecimal;

// STORY: FPMAPP-9037 - Service interface for currency rate override operations
public interface CurrencyRateOverrideService {

    /**
     * Override the currency exchange rate with audit logging and alert notification.
     *
     * @param currencyPair the currency pair to override (e.g. "USD/EUR")
     * @param rate the new overridden exchange rate
     * @param overrideReason reason for the override
     * @param userId the ID of the user performing the override
     * @return true if alert notification was sent successfully, false otherwise
     */
    boolean overrideCurrencyRate(String currencyPair, BigDecimal rate, String overrideReason, String userId);
}