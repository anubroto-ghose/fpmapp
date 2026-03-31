package com.fpm.repository;

import com.fpm.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    // STORY: FPMAPP-9042 - Find latest rate for currency pair at or before given timestamp
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.currencyPair = :currencyPair AND cr.timestamp <= :timestamp ORDER BY cr.timestamp DESC")
    List<CurrencyRate> findTopByCurrencyPairAndTimestampBeforeOrderByTimestampDesc(
            @Param("currencyPair") String currencyPair,
            @Param("timestamp") LocalDateTime timestamp);

    // STORY: FPMAPP-9042 - Find latest current rate for currency pair
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.currencyPair = :currencyPair ORDER BY cr.timestamp DESC")
    List<CurrencyRate> findTopByCurrencyPairOrderByTimestampDesc(@Param("currencyPair") String currencyPair);

    // STORY: FPMAPP-9042 - Find overrides for currency pair
    List<CurrencyRate> findByCurrencyPairAndOverrideFlagTrueOrderByEffectiveDateDesc(String currencyPair);

}
