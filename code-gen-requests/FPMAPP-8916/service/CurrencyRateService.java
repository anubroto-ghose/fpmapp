package com.fpm.service;

import com.fpm.model.CurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CurrencyRateService {

    // STORY: FPMAPP-8916 - Fetch latest exchange rate for a currency
    CurrencyRate getLatestRate(String currencyCode);

    // STORY: FPMAPP-8916 - Fetch historical exchange rates for a currency within date range
    List<CurrencyRate> getHistoricalRates(String currencyCode, LocalDateTime startDate, LocalDateTime endDate);

    // STORY: FPMAPP-8916 - Admin override of currency rate with audit logging
    CurrencyRate overrideCurrencyRate(String currencyCode, LocalDateTime rateDate, BigDecimal newRate, String overrideBy, String reason);

    // STORY: FPMAPP-8916 - Scheduled sync to fetch and update currency rates from third-party API
    void syncCurrencyRates();
}
