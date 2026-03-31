package com.fpm.service.impl;

import com.fpm.dto.CurrencyRateDto;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    private final CurrencyRateRepository currencyRateRepository;

    @Value("${currency.api.url}")
    private String currencyApiUrl;

    // TODO: Inject RestTemplate or WebClient for API calls

    public CurrencyServiceImpl(CurrencyRateRepository currencyRateRepository) {
        this.currencyRateRepository = currencyRateRepository;
    }

    // STORY: FPMAPP-8912 - Scheduled fetch of real-time currency rates
    @Scheduled(fixedDelayString = "${currency.fetch.interval.ms:3600000}") // default 1 hour
    @Transactional
    public void fetchAndStoreRealTimeRates() {
        logger.info("Fetching real-time currency rates from third-party API");

        // TODO: Implement actual API call to fetch rates
        // For demo, simulate fetching USD and EUR rates

        LocalDateTime now = LocalDateTime.now();

        // Simulated data
        saveOrUpdateRate("USD", new BigDecimal("1.00"), now, false, false, null);
        saveOrUpdateRate("EUR", new BigDecimal("0.85"), now, false, false, null);

        // TODO: Fetch all supported currencies and save
    }

    private void saveOrUpdateRate(String currencyCode, BigDecimal rate, LocalDateTime timestamp, boolean isHistorical, boolean adminOverrideFlag, String overrideReason) {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setCurrencyCode(currencyCode);
        currencyRate.setRate(rate);
        currencyRate.setRateTimestamp(timestamp);
        currencyRate.setHistorical(isHistorical);
        currencyRate.setAdminOverrideFlag(adminOverrideFlag);
        currencyRate.setOverrideReason(overrideReason);

        currencyRateRepository.save(currencyRate);
    }

    // STORY: FPMAPP-8912 - Get latest currency rate DTO
    @Transactional(readOnly = true)
    public CurrencyRateDto getLatestRate(String currencyCode) {
        return currencyRateRepository.findTopByCurrencyCodeOrderByRateTimestampDesc(currencyCode)
                .map(this::toDto)
                .orElse(null);
    }

    // STORY: FPMAPP-8912 - Get historical currency rates DTO list
    @Transactional(readOnly = true)
    public List<CurrencyRateDto> getHistoricalRates(String currencyCode, LocalDateTime start, LocalDateTime end) {
        List<CurrencyRate> rates = currencyRateRepository.findByCurrencyCodeAndRateTimestampBetweenOrderByRateTimestampAsc(currencyCode, start, end);
        return rates.stream().map(this::toDto).collect(Collectors.toList());
    }

    // STORY: FPMAPP-8912 - Admin override currency rate
    @Transactional
    public CurrencyRateDto overrideCurrencyRate(String currencyCode, double rate, LocalDateTime rateTimestamp, String overrideReason) {
        // Validate inputs
        if (overrideReason == null || overrideReason.isBlank()) {
            throw new IllegalArgumentException("Override reason must be provided");
        }

        CurrencyRate overriddenRate = new CurrencyRate();
        overriddenRate.setCurrencyCode(currencyCode);
        overriddenRate.setRate(BigDecimal.valueOf(rate));
        overriddenRate.setRateTimestamp(rateTimestamp);
        overriddenRate.setHistorical(false); // overrides are current
        overriddenRate.setAdminOverrideFlag(true);
        overriddenRate.setOverrideReason(overrideReason);

        currencyRateRepository.save(overriddenRate);

        // TODO: Trigger alert/notification for override
        logger.warn("Currency rate overridden for {} at {} with reason: {}", currencyCode, rateTimestamp, overrideReason);

        return toDto(overriddenRate);
    }

    private CurrencyRateDto toDto(CurrencyRate entity) {
        CurrencyRateDto dto = new CurrencyRateDto();
        dto.setCurrencyCode(entity.getCurrencyCode());
        dto.setRate(entity.getRate());
        dto.setRateTimestamp(entity.getRateTimestamp());
        dto.setHistorical(entity.isHistorical());
        dto.setAdminOverrideFlag(entity.isAdminOverrideFlag());
        dto.setOverrideReason(entity.getOverrideReason());
        return dto;
    }

}
