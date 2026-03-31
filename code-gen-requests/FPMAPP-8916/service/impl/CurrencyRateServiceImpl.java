package com.fpm.service.impl;

import com.fpm.dto.CurrencyRateDto;
import com.fpm.model.CurrencyRate;
import com.fpm.repository.CurrencyRateRepository;
import com.fpm.service.CurrencyRateService;
import com.fpm.util.CurrencyApiClient;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateServiceImpl.class);

    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyApiClient currencyApiClient;

    public CurrencyRateServiceImpl(CurrencyRateRepository currencyRateRepository, CurrencyApiClient currencyApiClient) {
        this.currencyRateRepository = currencyRateRepository;
        this.currencyApiClient = currencyApiClient;
    }

    @Override
    public CurrencyRateDto getLatestRate(String currencyCode) {
        // STORY: FPMAPP-8916 - Retrieve latest currency rate
        return currencyRateRepository.findTopByCurrencyCodeOrderByRateDateDesc(currencyCode)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public List<CurrencyRateDto> getHistoricalRates(String currencyCode, LocalDateTime start, LocalDateTime end) {
        // STORY: FPMAPP-8916 - Retrieve historical currency rates between dates
        List<CurrencyRate> rates = currencyRateRepository.findByCurrencyCodeAndRateDateBetweenOrderByRateDateDesc(currencyCode, start, end);
        return rates.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CurrencyRateDto overrideCurrencyRate(String currencyCode, LocalDateTime rateDate, String overrideBy, String reason, double newRate) {
        // STORY: FPMAPP-8916 - Admin override with audit logging and alerting
        // TODO: Validate admin privileges before allowing override

        CurrencyRate overriddenRate = new CurrencyRate();
        overriddenRate.setCurrencyCode(currencyCode);
        overriddenRate.setRateDate(rateDate);
        overriddenRate.setExchangeRate(BigDecimal.valueOf(newRate));
        overriddenRate.setOverridden(true);
        overriddenRate.setOverrideBy(overrideBy);
        overriddenRate.setOverrideReason(reason);
        overriddenRate.setOverrideAt(LocalDateTime.now());

        CurrencyRate saved = currencyRateRepository.save(overriddenRate);

        logger.info("Currency rate overridden by {} for {} on {} with new rate {}", overrideBy, currencyCode, rateDate, newRate);

        // TODO: Implement alerting mechanism (e.g. email or notification) for overrides

        return toDto(saved);
    }

    @Override
    @Scheduled(cron = "0 0 */1 * * *") // every hour
    public void syncCurrencyRates() {
        // STORY: FPMAPP-8916 - Scheduled job to sync currency rates from third-party API
        try {
            List<CurrencyRateDto> latestRates = currencyApiClient.fetchLatestRates();
            LocalDateTime now = LocalDateTime.now();

            for (CurrencyRateDto dto : latestRates) {
                CurrencyRate rate = new CurrencyRate();
                rate.setCurrencyCode(dto.getCurrencyCode());
                rate.setRateDate(now);
                rate.setExchangeRate(dto.getExchangeRate());
                rate.setOverridden(false);
                currencyRateRepository.save(rate);
            }

            logger.info("Currency rates synced successfully at {}", now);
        } catch (Exception e) {
            logger.error("Failed to sync currency rates: {}", e.getMessage(), e);
            // TODO: Implement alerting for sync failures
        }
    }

    private CurrencyRateDto toDto(CurrencyRate entity) {
        CurrencyRateDto dto = new CurrencyRateDto();
        dto.setCurrencyCode(entity.getCurrencyCode());
        dto.setRateDate(entity.getRateDate());
        dto.setExchangeRate(entity.getExchangeRate());
        dto.setOverridden(entity.isOverridden());
        dto.setOverrideBy(entity.getOverrideBy());
        dto.setOverrideReason(entity.getOverrideReason());
        dto.setOverrideAt(entity.getOverrideAt());
        return dto;
    }

}
