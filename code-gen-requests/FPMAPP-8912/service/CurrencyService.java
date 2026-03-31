package com.fpm.service;

import com.fpm.dto.CurrencyRateResponseDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CurrencyService {

    // STORY: FPMAPP-8912 - Fetch and store real-time currency rates from third-party API
    void fetchAndStoreRealTimeRates();

    // STORY: FPMAPP-8912 - Get latest currency rate for a currency code
    CurrencyRateResponseDTO getLatestRate(String currencyCode);

    // STORY: FPMAPP-8912 - Get historical currency rates for a currency code between dates
    List<CurrencyRateResponseDTO> getHistoricalRates(String currencyCode, LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8912 - Admin override of currency rate with reason
    CurrencyRateResponseDTO overrideCurrencyRate(String currencyCode, BigDecimal newRate, LocalDateTime rateTimestamp, String overrideReason);

}
