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

    // STORY: FPMAPP-8916 - Find currency rates between dates for historical queries
    List<CurrencyRate> findByCurrencyCodeAndRateDateBetweenOrderByRateDateDesc(String currencyCode, LocalDateTime start, LocalDateTime end);

    // STORY: FPMAPP-8916 - Find latest rate for currency
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.currencyCode = :currencyCode AND cr.rateDate = (SELECT MAX(cr2.rateDate) FROM CurrencyRate cr2 WHERE cr2.currencyCode = :currencyCode)")
    Optional<CurrencyRate> findTopByCurrencyCodeOrderByRateDateDesc(@Param("currencyCode") String currencyCode);

}
