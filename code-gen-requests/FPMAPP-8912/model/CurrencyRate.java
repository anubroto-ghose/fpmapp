package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_exchange_rates")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @Column(name = "rate_timestamp", nullable = false)
    private LocalDateTime rateTimestamp;

    @Column(name = "is_historical", nullable = false)
    private boolean isHistorical;

    @Column(name = "admin_override_flag", nullable = false)
    private boolean adminOverrideFlag;

    @Column(name = "override_reason", length = 500)
    private String overrideReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // STORY: FPMAPP-8912 - Added fields for timestamp, historical flag, admin override and reason

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters

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

    public boolean isHistorical() {
        return isHistorical;
    }

    public void setHistorical(boolean historical) {
        isHistorical = historical;
    }

    public boolean isAdminOverrideFlag() {
        return adminOverrideFlag;
    }

    public void setAdminOverrideFlag(boolean adminOverrideFlag) {
        this.adminOverrideFlag = adminOverrideFlag;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
