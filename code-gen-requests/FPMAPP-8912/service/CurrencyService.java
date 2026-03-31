package com.fpm.service;

import com.fpm.dto.CurrencyRateDto;
import java.time.LocalDateTime;
import java.util.List;

public interface CurrencyService {

    // STORY: FPMAPP-8912 - Fetch and store real-time currency rates from third-party API
    void fetchAndStoreRealTimeRates();

    // STORY: FPMAPP-8912 - Get latest currency rate for a given currency code
    CurrencyRateDto getLatestRate(String currencyCode);

    // STORY: FPMAPP-8912 - Get historical currency rates for a currency code between dates
    List<CurrencyRateDto> getHistoricalRates(String currencyCode, LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8912 - Admin override of currency rate with reason
    CurrencyRateDto overrideCurrencyRate(String currencyCode, double rate, LocalDateTime rateTimestamp, String overrideReason);

}
