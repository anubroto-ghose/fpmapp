package com.fpm.repository;

import com.fpm.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    // STORY: FPMAPP-8916 - Find latest currency rate for a given currency code
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.currencyCode = :currencyCode ORDER BY cr.rateDate DESC")
    List<CurrencyRate> findLatestByCurrencyCode(@Param("currencyCode") String currencyCode);

    // STORY: FPMAPP-8916 - Find currency rate by currency code and exact rate date
    Optional<CurrencyRate> findByCurrencyCodeAndRateDate(String currencyCode, LocalDateTime rateDate);

    // STORY: FPMAPP-8916 - Find all currency rates for a currency code between dates
    List<CurrencyRate> findByCurrencyCodeAndRateDateBetweenOrderByRateDateAsc(String currencyCode, LocalDateTime startDate, LocalDateTime endDate);

    // STORY: FPMAPP-8916 - Find latest rate date for a currency
    @Query("SELECT MAX(cr.rateDate) FROM CurrencyRate cr WHERE cr.currencyCode = :currencyCode")
    LocalDateTime findLatestRateDate(@Param("currencyCode") String currencyCode);
}
