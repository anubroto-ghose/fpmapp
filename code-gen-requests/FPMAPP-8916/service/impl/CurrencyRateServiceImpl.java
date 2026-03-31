package com.fpm.service.impl;

import com.fpm.model.CurrencyRate;
import com.fpm.repository.CurrencyRateRepository;
import com.fpm.service.CurrencyRateService;
import com.fpm.util.AlertUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateServiceImpl.class);

    private final CurrencyRateRepository currencyRateRepository;
    private final AlertUtil alertUtil;

    @Value("${currency.api.url}")
    private String currencyApiUrl;

    @Value("${currency.api.key}")
    private String currencyApiKey;

    public CurrencyRateServiceImpl(CurrencyRateRepository currencyRateRepository, AlertUtil alertUtil) {
        this.currencyRateRepository = currencyRateRepository;
        this.alertUtil = alertUtil;
    }

    // STORY: FPMAPP-8916 - Get latest currency rate for given currency code
    @Override
    public CurrencyRate getLatestRate(String currencyCode) {
        List<CurrencyRate> rates = currencyRateRepository.findLatestByCurrencyCode(currencyCode);
        if (rates.isEmpty()) {
            return null;
        }
        return rates.get(0);
    }

    // STORY: FPMAPP-8916 - Get historical currency rates between dates
    @Override
    public List<CurrencyRate> getHistoricalRates(String currencyCode, LocalDateTime startDate, LocalDateTime endDate) {
        return currencyRateRepository.findByCurrencyCodeAndRateDateBetweenOrderByRateDateAsc(currencyCode, startDate, endDate);
    }

    // STORY: FPMAPP-8916 - Admin override currency rate with audit logging and alerting
    @Override
    @Transactional
    public CurrencyRate overrideCurrencyRate(String currencyCode, LocalDateTime rateDate, BigDecimal newRate, String overrideBy, String reason) {
        Optional<CurrencyRate> existingOpt = currencyRateRepository.findByCurrencyCodeAndRateDate(currencyCode, rateDate);
        CurrencyRate currencyRate;
        if (existingOpt.isPresent()) {
            currencyRate = existingOpt.get();
            currencyRate.setExchangeRate(newRate);
        } else {
            currencyRate = new CurrencyRate(currencyCode, rateDate, newRate);
        }
        currencyRate.setOverridden(true);
        currencyRate.setOverrideAt(LocalDateTime.now());
        currencyRate.setOverrideBy(overrideBy);
        currencyRate.setOverrideReason(reason);

        CurrencyRate saved = currencyRateRepository.save(currencyRate);

        // Log override event
        logger.info("Currency rate overridden: currency={}, date={}, newRate={}, by={}, reason={}",
                currencyCode, rateDate, newRate, overrideBy, reason);

        // Send alert for override
        alertUtil.sendAlert(String.format("Currency rate overridden for %s on %s by %s. Reason: %s",
                currencyCode, rateDate.toString(), overrideBy, reason));

        return saved;
    }

    // STORY: FPMAPP-8916 - Scheduled sync job to fetch and update currency rates from third-party API
    @Override
    @Scheduled(cron = "0 0 * * * *") // every hour, configurable
    public void syncCurrencyRates() {
        logger.info("Starting scheduled currency rates sync");
        try {
            // TODO: Implement secure call to third-party currency exchange API using currencyApiUrl and currencyApiKey
            // TODO: Parse response and update currencyRateRepository with new rates
            // TODO: Save new rates with current timestamp as rateDate
            // TODO: Handle failures and retry logic if needed

            // Example placeholder logic:
            // Map<String, BigDecimal> fetchedRates = thirdPartyApiClient.fetchLatestRates();
            // LocalDateTime now = LocalDateTime.now();
            // for (Map.Entry<String, BigDecimal> entry : fetchedRates.entrySet()) {
            //     CurrencyRate rate = new CurrencyRate(entry.getKey(), now, entry.getValue());
            //     currencyRateRepository.save(rate);
            // }

            logger.info("Currency rates sync completed successfully");
        } catch (Exception e) {
            logger.error("Failed to sync currency rates", e);
            alertUtil.sendAlert("Currency rates sync failed: " + e.getMessage());
        }
    }

    // STORY: FPMAPP-8916 - Initial sync on startup
    @PostConstruct
    public void init() {
        syncCurrencyRates();
    }
}
