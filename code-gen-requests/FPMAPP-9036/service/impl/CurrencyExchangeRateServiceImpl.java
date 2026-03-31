package com.fpm.service.impl;

import com.fpm.dto.CurrencyRateOverrideRequest;
import com.fpm.dto.CurrencyRateResponse;
import com.fpm.model.CurrencyExchangeRate;
import com.fpm.repository.CurrencyExchangeRateRepository;
import com.fpm.service.CurrencyExchangeRateService;
import com.fpm.util.EmailUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurrencyExchangeRateServiceImpl implements CurrencyExchangeRateService {

    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;
    private final EmailUtil emailUtil;

    @Value("${fpm.currency.override.alert.to}")
    private String alertEmailTo;

    public CurrencyExchangeRateServiceImpl(CurrencyExchangeRateRepository currencyExchangeRateRepository, EmailUtil emailUtil) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
        this.emailUtil = emailUtil;
    }

    // STORY: FPMAPP-9036 - get currency rates by optional timestamp
    @Override
    public List<CurrencyRateResponse> getCurrencyRates(LocalDateTime timestamp) {
        List<CurrencyExchangeRate> rates;
        if (timestamp == null) {
            // get latest rates for all currency pairs
            rates = currencyExchangeRateRepository.findLatestRatesForAllPairs();
        } else {
            // TODO: optimize query to get latest rate per currency pair before timestamp
            // For simplicity, get all currency pairs distinct and fetch latest before timestamp
            // This is a simplified approach; in production consider native query or window functions
            rates = new ArrayList<>();
            List<String> currencyPairs = currencyExchangeRateRepository.findAll()
                    .stream()
                    .map(CurrencyExchangeRate::getCurrencyPair)
                    .distinct()
                    .toList();
            for (String pair : currencyPairs) {
                List<CurrencyExchangeRate> list = currencyExchangeRateRepository.findTopByCurrencyPairAndRateTimestampBeforeOrderByRateTimestampDesc(pair, timestamp);
                if (!CollectionUtils.isEmpty(list)) {
                    rates.add(list.get(0));
                }
            }
        }

        return rates.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CurrencyRateResponse toResponse(CurrencyExchangeRate rate) {
        CurrencyRateResponse resp = new CurrencyRateResponse();
        resp.setCurrencyPair(rate.getCurrencyPair());
        resp.setRate(rate.getRate());
        resp.setRateTimestamp(rate.getRateTimestamp());
        resp.setOverride(rate.isOverride());
        resp.setOverrideReason(rate.getOverrideReason());
        return resp;
    }

    // STORY: FPMAPP-9036 - override currency rate with reason and user info
    @Override
    @Transactional
    public boolean overrideCurrencyRate(CurrencyRateOverrideRequest request, Integer updatedByUserId) {
        // Validate input
        if (request.getCurrencyPair() == null || request.getRate() == null || request.getOverrideReason() == null) {
            return false;
        }

        // Create new override rate record
        CurrencyExchangeRate overrideRate = new CurrencyExchangeRate();
        overrideRate.setCurrencyPair(request.getCurrencyPair());
        overrideRate.setRate(request.getRate());
        overrideRate.setRateTimestamp(LocalDateTime.now());
        overrideRate.setOverride(true);
        overrideRate.setOverrideReason(request.getOverrideReason());
        overrideRate.setUpdatedByUserId(updatedByUserId);

        currencyExchangeRateRepository.save(overrideRate);

        // Send alert email
        boolean alertSent = sendOverrideAlertEmail(overrideRate);

        return alertSent;
    }

    private boolean sendOverrideAlertEmail(CurrencyExchangeRate overrideRate) {
        try {
            String subject = "[FPMTools] Currency Rate Override Alert: " + overrideRate.getCurrencyPair();
            String body = "Currency rate override performed by user ID: " + overrideRate.getUpdatedByUserId() + "\n"
                    + "Currency Pair: " + overrideRate.getCurrencyPair() + "\n"
                    + "New Rate: " + overrideRate.getRate() + "\n"
                    + "Override Reason: " + overrideRate.getOverrideReason() + "\n"
                    + "Timestamp: " + overrideRate.getRateTimestamp() + "\n";
            emailUtil.sendEmail(alertEmailTo, subject, body);
            return true;
        } catch (Exception e) {
            // TODO: log error
            return false;
        }
    }

    // STORY: FPMAPP-9036 - scheduled synchronization with third-party API
    @Override
    @Scheduled(cron = "0 0 * * * *") // every hour
    public void synchronizeCurrencyRates() {
        // TODO: Implement third-party API integration to fetch real-time and historical rates
        // For now, this is a stub method

        // Example pseudocode:
        // List<CurrencyRateDto> fetchedRates = thirdPartyClient.fetchLatestRates();
        // for each rate:
        //   check if exists and newer, then save

        // TODO: handle errors and retries
    }

}
