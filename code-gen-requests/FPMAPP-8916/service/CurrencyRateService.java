package com.fpm.service;

import com.fpm.dto.CurrencyRateDto;
import com.fpm.model.CurrencyRate;

import java.time.LocalDateTime;
import java.util.List;

public interface CurrencyRateService {

    // STORY: FPMAPP-8916 - Fetch latest currency rate for given currency code
    CurrencyRateDto getLatestRate(String currencyCode);

    // STORY: FPMAPP-8916 - Fetch historical currency rates for given currency code and date range
    List<CurrencyRateDto> getHistoricalRates(String currencyCode, LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8916 - Admin override of currency rate with audit logging
    CurrencyRateDto overrideCurrencyRate(String currencyCode, LocalDateTime rateDate, String overrideBy, String reason, double newRate);

    // STORY: FPMAPP-8916 - Scheduled sync to fetch and update currency rates from third-party API
    void syncCurrencyRates();

}
