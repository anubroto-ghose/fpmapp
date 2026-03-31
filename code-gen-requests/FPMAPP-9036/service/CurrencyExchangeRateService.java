package com.fpm.service;

import com.fpm.dto.CurrencyRateOverrideRequest;
import com.fpm.dto.CurrencyRateResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CurrencyExchangeRateService {

    // STORY: FPMAPP-9036 - get currency rates by optional timestamp
    List<CurrencyRateResponse> getCurrencyRates(LocalDateTime timestamp);

    // STORY: FPMAPP-9036 - override currency rate with reason and user info
    boolean overrideCurrencyRate(CurrencyRateOverrideRequest request, Integer updatedByUserId);

    // STORY: FPMAPP-9036 - scheduled synchronization with third-party API
    void synchronizeCurrencyRates();

}
