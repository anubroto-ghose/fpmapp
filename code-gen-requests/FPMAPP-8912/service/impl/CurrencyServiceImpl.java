package com.fpm.service.impl;

import com.fpm.dto.CurrencyRateResponseDTO;
import com.fpm.model.CurrencyRate;
import com.fpm.repository.CurrencyRateRepository;
import com.fpm.service.CurrencyService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    private final CurrencyRateRepository currencyRateRepository;

    // TODO: Inject your third-party currency API client here
    // private final CurrencyApiClient currencyApiClient;

    @Value("${currency.fetch.interval.millis:3600000}")
    private long fetchIntervalMillis;

    public CurrencyServiceImpl(CurrencyRateRepository currencyRateRepository) {
        this.currencyRateRepository = currencyRateRepository;
    }

    @PostConstruct
    public void init() {
        // STORY: FPMAPP-8912 - Initial fetch of real-time currency rates on startup
        fetchAndStoreRealTimeRates();
    }

    @Override
    @Scheduled(fixedDelayString = "${currency.fetch.interval.millis:3600000}")
    public void fetchAndStoreRealTimeRates() {
        // STORY: FPMAPP-8912 - Scheduled fetch and store real-time currency rates
        logger.info("Fetching real-time currency rates from third-party API");

        // TODO: Implement actual call to third-party API to fetch rates
        // Example placeholder data:
        List<CurrencyRate> fetchedRates = new ArrayList<>();

        // TODO: Replace with real API call and parsing
        // For example, assume we get a map of currencyCode -> rate
        // Map<String, BigDecimal> ratesFromApi = currencyApiClient.getLatestRates();

        // Simulated example for USD and EUR
        LocalDateTime now = LocalDateTime.now();
        fetchedRates.add(createCurrencyRate("USD", new BigDecimal("1.000000"), now, false, false, null));
        fetchedRates.add(createCurrencyRate("EUR", new BigDecimal("0.920000"), now, false, false, null));

        for (CurrencyRate rate : fetchedRates) {
            // Save only if no admin override exists for this timestamp and currency
            Optional<CurrencyRate> existing = currencyRateRepository.findByCurrencyCodeAndRateTimestamp(rate.getCurrencyCode(), rate.getRateTimestamp());
            if (existing.isEmpty()) {
                currencyRateRepository.save(rate);
            } else {
                logger.debug("Rate for {} at {} already exists, skipping save", rate.getCurrencyCode(), rate.getRateTimestamp());
            }
        }

        logger.info("Completed fetching and storing real-time currency rates");
    }

    private CurrencyRate createCurrencyRate(String currencyCode, BigDecimal rate, LocalDateTime timestamp, boolean isHistorical, boolean adminOverrideFlag, String overrideReason) {
        CurrencyRate cr = new CurrencyRate();
        cr.setCurrencyCode(currencyCode);
        cr.setRate(rate);
        cr.setRateTimestamp(timestamp);
        cr.setHistorical(isHistorical);
        cr.setAdminOverrideFlag(adminOverrideFlag);
        cr.setOverrideReason(overrideReason);
        return cr;
    }

    @Override
    public CurrencyRateResponseDTO getLatestRate(String currencyCode) {
        // STORY: FPMAPP-8912 - Retrieve latest currency rate with override status and timestamps
        Optional<CurrencyRate> latestOpt = currencyRateRepository.findTopByCurrencyCodeOrderByRateTimestampDesc(currencyCode);
        if (latestOpt.isEmpty()) {
            return null; // TODO: Consider throwing custom exception or returning Optional
        }
        CurrencyRate rate = latestOpt.get();
        return mapToDto(rate);
    }

    @Override
    public List<CurrencyRateResponseDTO> getHistoricalRates(String currencyCode, LocalDateTime start, LocalDateTime end) {
        // STORY: FPMAPP-8912 - Retrieve historical currency rates between dates
        List<CurrencyRate> rates = currencyRateRepository.findHistoricalRatesBetween(currencyCode, start, end);
        List<CurrencyRateResponseDTO> dtos = new ArrayList<>();
        for (CurrencyRate rate : rates) {
            dtos.add(mapToDto(rate));
        }
        return dtos;
    }

    @Override
    @Transactional
    public CurrencyRateResponseDTO overrideCurrencyRate(String currencyCode, BigDecimal newRate, LocalDateTime rateTimestamp, String overrideReason) {
        // STORY: FPMAPP-8912 - Admin override of currency rate with reason and alerting

        // Validate inputs
        if (overrideReason == null || overrideReason.isBlank()) {
            throw new IllegalArgumentException("Override reason must be provided");
        }

        // Find existing rate for the timestamp or create new
        Optional<CurrencyRate> existingOpt = currencyRateRepository.findByCurrencyCodeAndRateTimestamp(currencyCode, rateTimestamp);
        CurrencyRate rate;
        if (existingOpt.isPresent()) {
            rate = existingOpt.get();
            rate.setRate(newRate);
            rate.setAdminOverrideFlag(true);
            rate.setOverrideReason(overrideReason);
            rate.setHistorical(false); // Overrides are current
        } else {
            rate = createCurrencyRate(currencyCode, newRate, rateTimestamp, false, true, overrideReason);
        }

        CurrencyRate saved = currencyRateRepository.save(rate);

        // TODO: Trigger alert/notification about override (e.g. email, message queue)
        logger.warn("Currency rate overridden for {} at {} with reason: {}", currencyCode, rateTimestamp, overrideReason);

        return mapToDto(saved);
    }

    private CurrencyRateResponseDTO mapToDto(CurrencyRate rate) {
        CurrencyRateResponseDTO dto = new CurrencyRateResponseDTO();
        dto.setCurrencyCode(rate.getCurrencyCode());
        dto.setRate(rate.getRate());
        dto.setRateTimestamp(rate.getRateTimestamp());
        dto.setHistorical(rate.isHistorical());
        dto.setAdminOverrideFlag(rate.isAdminOverrideFlag());
        dto.setOverrideReason(rate.getOverrideReason());
        return dto;
    }

}
