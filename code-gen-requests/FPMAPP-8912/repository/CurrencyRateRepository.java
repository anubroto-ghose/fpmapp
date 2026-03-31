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

    // STORY: FPMAPP-8912 - Find all historical rates for a currency between dates
    @Query("SELECT c FROM CurrencyRate c WHERE c.currencyCode = :currencyCode AND c.rateTimestamp BETWEEN :start AND :end ORDER BY c.rateTimestamp ASC")
    List<CurrencyRate> findHistoricalRatesBetween(@Param("currencyCode") String currencyCode,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    // STORY: FPMAPP-8912 - Find all current (non-historical) rates
    List<CurrencyRate> findByIsHistoricalFalse();

}
