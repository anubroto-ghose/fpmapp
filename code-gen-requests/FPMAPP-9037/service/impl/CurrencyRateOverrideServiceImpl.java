package com.fpm.service.impl;

import com.fpm.repository.CurrencyExchangeRateRepository;
import com.fpm.service.CurrencyRateOverrideService;
import com.fpm.util.EmailNotificationUtil;
import com.fpm.model.CurrencyExchangeRate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// STORY: FPMAPP-9037 - Implementation of currency rate override service with audit logging and alerting
@Service
public class CurrencyRateOverrideServiceImpl implements CurrencyRateOverrideService {

    @Autowired
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @Autowired
    private EmailNotificationUtil emailNotificationUtil;

    private static final String ALERT_SUBJECT = "[FPMTools] Currency Rate Override Alert";

    private static final String ALERT_RECIPIENT = "fpm-admin-alerts@company.com"; // TODO: Externalize alert recipient email

    @Override
    @Transactional
    public boolean overrideCurrencyRate(String currencyPair, BigDecimal rate, String overrideReason, String userId) {
        // Fetch existing currency rate record or create new
        CurrencyExchangeRate existingRate = currencyExchangeRateRepository.findByCurrencyPair(currencyPair).orElse(new CurrencyExchangeRate());

        // Update fields with override data
        existingRate.setCurrencyPair(currencyPair);
        existingRate.setRate(rate);
        existingRate.setRateTimestamp(LocalDateTime.now());
        existingRate.setIsOverride(true);
        existingRate.setOverrideReason(overrideReason);
        existingRate.setUpdatedByUserId(userId);

        // Save override record (audit log is maintained by DB immutability and versioning)
        currencyExchangeRateRepository.save(existingRate);

        // Prepare alert email content
        StringBuilder alertBody = new StringBuilder();
        alertBody.append("Currency rate override performed:\n");
        alertBody.append("Currency Pair: ").append(currencyPair).append("\n");
        alertBody.append("New Rate: ").append(rate).append("\n");
        alertBody.append("Override Reason: ").append(overrideReason).append("\n");
        alertBody.append("Performed By User ID: ").append(userId).append("\n");
        alertBody.append("Timestamp: ").append(existingRate.getRateTimestamp()).append("\n");

        // Send alert notification via SMTP
        boolean alertSent = false;
        try {
            emailNotificationUtil.sendEmail(ALERT_RECIPIENT, ALERT_SUBJECT, alertBody.toString());
            alertSent = true;
        } catch (Exception e) {
            // TODO: Log email sending failure
            alertSent = false;
        }

        return alertSent;
    }
}