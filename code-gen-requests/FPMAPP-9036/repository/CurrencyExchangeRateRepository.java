package com.fpm.repository;

import com.fpm.model.CurrencyExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRate, Long> {

    // STORY: FPMAPP-9036 - find rates by currency pair and timestamp (historical query)
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.currencyPair = :currencyPair AND c.rateTimestamp <= :timestamp ORDER BY c.rateTimestamp DESC")
    List<CurrencyExchangeRate> findTopByCurrencyPairAndRateTimestampBeforeOrderByRateTimestampDesc(
            @Param("currencyPair") String currencyPair,
            @Param("timestamp") LocalDateTime timestamp);

    // STORY: FPMAPP-9036 - find latest rate for currency pair
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.currencyPair = :currencyPair ORDER BY c.rateTimestamp DESC")
    List<CurrencyExchangeRate> findTopByCurrencyPairOrderByRateTimestampDesc(@Param("currencyPair") String currencyPair);

    // STORY: FPMAPP-9036 - find all rates at a given timestamp or latest if null
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE (:timestamp IS NULL OR c.rateTimestamp = :timestamp) ORDER BY c.currencyPair ASC")
    List<CurrencyExchangeRate> findByRateTimestamp(@Param("timestamp") LocalDateTime timestamp);

    // STORY: FPMAPP-9036 - find latest rates for all currency pairs
    @Query("SELECT c FROM CurrencyExchangeRate c WHERE c.rateTimestamp = (SELECT MAX(c2.rateTimestamp) FROM CurrencyExchangeRate c2 WHERE c2.currencyPair = c.currencyPair)")
    List<CurrencyExchangeRate> findLatestRatesForAllPairs();

    // STORY: FPMAPP-9036 - find overridden rates
    List<CurrencyExchangeRate> findByIsOverrideTrue();

}
