package com.fpm.service.impl;

import com.fpm.dto.CurrencyRateDTO;
import com.fpm.dto.CurrencyRateOverrideRequest;
import com.fpm.model.CurrencyRate;
import com.fpm.repository.CurrencyRateRepository;
import com.fpm.service.CurrencyRateService;
import com.fpm.util.EmailUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private final CurrencyRateRepository currencyRateRepository;
    private final EmailUtil emailUtil;

    @Value("${currency.provider.api.url}")
    private String currencyProviderApiUrl;

    @Value("${currency.override.alert.email}")
    private String alertEmail;

    public CurrencyRateServiceImpl(CurrencyRateRepository currencyRateRepository, EmailUtil emailUtil) {
        this.currencyRateRepository = currencyRateRepository;
        this.emailUtil = emailUtil;
    }

    // STORY: FPMAPP-9042 - Fetch current or historical currency rate
    @Override
    public CurrencyRateDTO getCurrencyRate(String currencyPair, LocalDateTime timestamp) {
        List<CurrencyRate> rates;
        if (timestamp != null) {
            rates = currencyRateRepository.findTopByCurrencyPairAndTimestampBeforeOrderByTimestampDesc(currencyPair, timestamp);
        } else {
            rates = currencyRateRepository.findTopByCurrencyPairOrderByTimestampDesc(currencyPair);
        }
        if (rates.isEmpty()) {
            return null; // TODO: Consider throwing custom exception for not found
        }
        CurrencyRate rate = rates.get(0);
        return mapToDTO(rate);
    }

    // STORY: FPMAPP-9042 - Admin override of currency rate
    @Override
    @Transactional
    public CurrencyRateDTO overrideCurrencyRate(CurrencyRateOverrideRequest request) {
        // Validate input
        if (!StringUtils.hasText(request.getCurrencyPair()) || request.getOverrideRate() == null || !StringUtils.hasText(request.getReason()) || request.getEffectiveDate() == null) {
            throw new IllegalArgumentException("Invalid override request parameters");
        }

        CurrencyRate overrideRate = new CurrencyRate();
        overrideRate.setCurrencyPair(request.getCurrencyPair());
        overrideRate.setRate(request.getOverrideRate());
        overrideRate.setTimestamp(LocalDateTime.now());
        overrideRate.setOverrideFlag(true);
        overrideRate.setOverrideReason(request.getReason());
        overrideRate.setEffectiveDate(request.getEffectiveDate());

        currencyRateRepository.save(overrideRate);

        // STORY: FPMAPP-9042 - Send alert notification via SMTP
        sendOverrideAlertEmail(overrideRate);

        return mapToDTO(overrideRate);
    }

    private void sendOverrideAlertEmail(CurrencyRate overrideRate) {
        String subject = "Currency Rate Override Alert: " + overrideRate.getCurrencyPair();
        String body = String.format("Currency pair %s overridden with rate %s effective %s. Reason: %s",
                overrideRate.getCurrencyPair(),
                overrideRate.getRate().toPlainString(),
                overrideRate.getEffectiveDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                overrideRate.getOverrideReason());
        // TODO: Add more detailed info if needed
        emailUtil.sendEmail(alertEmail, subject, body);
    }

    // STORY: FPMAPP-9042 - Synchronize currency rates from third-party provider
    @Override
    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void synchronizeCurrencyRates() {
        // TODO: Implement third-party API call to fetch latest currency rates
        // For example, call currencyProviderApiUrl, parse response, and save new CurrencyRate entries

        // Pseudocode:
        // Map<String, BigDecimal> latestRates = thirdPartyClient.fetchLatestRates();
        // LocalDateTime now = LocalDateTime.now();
        // for (Map.Entry<String, BigDecimal> entry : latestRates.entrySet()) {
        //     CurrencyRate rate = new CurrencyRate(entry.getKey(), entry.getValue(), now);
        //     currencyRateRepository.save(rate);
        // }
    }

    // STORY: FPMAPP-9042 - Retrieve override audit logs for currency pair
    @Override
    public List<CurrencyRateDTO> getOverrides(String currencyPair) {
        List<CurrencyRate> overrides = currencyRateRepository.findByCurrencyPairAndOverrideFlagTrueOrderByEffectiveDateDesc(currencyPair);
        return overrides.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private CurrencyRateDTO mapToDTO(CurrencyRate rate) {
        CurrencyRateDTO dto = new CurrencyRateDTO();
        dto.setCurrencyPair(rate.getCurrencyPair());
        dto.setRate(rate.getRate());
        dto.setTimestamp(rate.getTimestamp());
        dto.setOverrideFlag(rate.isOverrideFlag());
        dto.setOverrideReason(rate.getOverrideReason());
        dto.setEffectiveDate(rate.getEffectiveDate());
        return dto;
    }

}
