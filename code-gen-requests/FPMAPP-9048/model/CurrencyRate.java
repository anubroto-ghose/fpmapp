package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rates")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_pair", nullable = false, length = 7)
    private String currencyPair;

    @Column(name = "rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @Column(name = "rate_timestamp", nullable = false)
    private LocalDateTime rateTimestamp;

    @Column(name = "is_override", nullable = false)
    private boolean override;

    // STORY: FPMAPP-9048 - Added fields for historical timestamp and override flag

    public CurrencyRate() {
    }

    public CurrencyRate(String currencyPair, BigDecimal rate, LocalDateTime rateTimestamp, boolean override) {
        this.currencyPair = currencyPair;
        this.rate = rate;
        this.rateTimestamp = rateTimestamp;
        this.override = override;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDateTime getRateTimestamp() {
        return rateTimestamp;
    }

    public void setRateTimestamp(LocalDateTime rateTimestamp) {
        this.rateTimestamp = rateTimestamp;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }
}
