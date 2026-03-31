package com.fpm.repository;

import com.fpm.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    // STORY: FPMAPP-8912 - Find latest currency rate for a given currency code
    Optional<CurrencyRate> findTopByCurrencyCodeOrderByRateTimestampDesc(String currencyCode);

    // STORY: FPMAPP-8912 - Find currency rate by currency code and exact timestamp
    Optional<CurrencyRate> findByCurrencyCodeAndRateTimestamp(String currencyCode, LocalDateTime rateTimestamp);

    // STORY: FPMAPP-8912 - Find all currency rates for a currency code between two timestamps (historical data)
    List<CurrencyRate> findByCurrencyCodeAndRateTimestampBetweenOrderByRateTimestampAsc(String currencyCode, LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8912 - Find all overridden rates for alerting or audit
    List<CurrencyRate> findByAdminOverrideFlagTrueOrderByRateTimestampDesc();

    // TODO: Add custom queries if needed for performance or complex filtering
}
