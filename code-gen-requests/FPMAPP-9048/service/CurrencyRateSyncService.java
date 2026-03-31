package com.fpm.service;

import com.fpm.model.CurrencyRateOverride;
import jakarta.mail.MessagingException;

public interface CurrencyRateSyncService {

    // STORY: FPMAPP-9048 - Trigger synchronization of currency rates from third-party provider
    void syncRatesFromProvider() throws Exception;

    // STORY: FPMAPP-9048 - Send email alert for admin override
    void sendOverrideAlertEmail(CurrencyRateOverride override) throws MessagingException;

}
