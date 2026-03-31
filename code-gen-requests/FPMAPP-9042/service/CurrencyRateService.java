package com.fpm.service;

import com.fpm.dto.CurrencyRateDTO;
import com.fpm.dto.CurrencyRateOverrideRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CurrencyRateService {

    // STORY: FPMAPP-9042 - Fetch current or historical currency rate
    CurrencyRateDTO getCurrencyRate(String currencyPair, LocalDateTime timestamp);

    // STORY: FPMAPP-9042 - Admin override of currency rate
    CurrencyRateDTO overrideCurrencyRate(CurrencyRateOverrideRequest request);

    // STORY: FPMAPP-9042 - Synchronize currency rates from third-party provider
    void synchronizeCurrencyRates();

    // STORY: FPMAPP-9042 - Retrieve override audit logs for currency pair
    List<CurrencyRateDTO> getOverrides(String currencyPair);

}
