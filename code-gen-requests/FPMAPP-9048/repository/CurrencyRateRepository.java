package com.fpm.repository;

import com.fpm.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    // STORY: FPMAPP-9048 - Find latest rate for a currency pair
    Optional<CurrencyRate> findTopByCurrencyPairOrderByRateTimestampDesc(String currencyPair);

    // STORY: FPMAPP-9048 - Find rate for a currency pair at or before a given timestamp
    @Query("SELECT cr FROM CurrencyRate cr WHERE cr.currencyPair = :currencyPair AND cr.rateTimestamp <= :timestamp ORDER BY cr.rateTimestamp DESC")
    List<CurrencyRate> findRatesByCurrencyPairAndTimestamp(@Param("currencyPair") String currencyPair, @Param("timestamp") LocalDateTime timestamp);

    // STORY: FPMAPP-9048 - Find all rates for a currency pair ordered by timestamp descending
    List<CurrencyRate> findByCurrencyPairOrderByRateTimestampDesc(String currencyPair);

}
