package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rates", indexes = {
    @Index(name = "idx_currency_date", columnList = "currency_code, rate_date")
})
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "rate_date", nullable = false)
    private LocalDateTime rateDate;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "is_overridden", nullable = false)
    private boolean overridden = false;

    @Column(name = "override_reason", length = 500)
    private String overrideReason;

    @Column(name = "override_by", length = 100)
    private String overrideBy;

    @Column(name = "override_at")
    private LocalDateTime overrideAt;

    // STORY: FPMAPP-8916 - Entity to store currency exchange rates with historical data and override info

    public CurrencyRate() {
    }

    public CurrencyRate(String currencyCode, LocalDateTime rateDate, BigDecimal exchangeRate) {
        this.currencyCode = currencyCode;
        this.rateDate = rateDate;
        this.exchangeRate = exchangeRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public LocalDateTime getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDateTime rateDate) {
        this.rateDate = rateDate;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public boolean isOverridden() {
        return overridden;
    }

    public void setOverridden(boolean overridden) {
        this.overridden = overridden;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }

    public String getOverrideBy() {
        return overrideBy;
    }

    public void setOverrideBy(String overrideBy) {
        this.overrideBy = overrideBy;
    }

    public LocalDateTime getOverrideAt() {
        return overrideAt;
    }

    public void setOverrideAt(LocalDateTime overrideAt) {
        this.overrideAt = overrideAt;
    }
}
