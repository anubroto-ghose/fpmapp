package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_exchange_rates", indexes = {
    @Index(name = "idx_rate_timestamp", columnList = "rateTimestamp"),
    @Index(name = "idx_is_override", columnList = "isOverride")
})
public class CurrencyExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 7) // e.g. USD/EUR
    private String currencyPair;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @Column(nullable = false)
    private LocalDateTime rateTimestamp;

    @Column(nullable = false)
    private boolean isOverride = false;

    @Column(length = 255)
    private String overrideReason;

    @Column
    private Integer updatedByUserId;

    // STORY: FPMAPP-9036 - add new columns for override and timestamp

    // Getters and setters

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
        return isOverride;
    }

    public void setOverride(boolean override) {
        isOverride = override;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }

    public Integer getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(Integer updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }
}
