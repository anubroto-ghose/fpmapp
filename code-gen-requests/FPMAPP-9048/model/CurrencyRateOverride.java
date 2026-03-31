package com.fpm.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_rate_overrides")
public class CurrencyRateOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_pair", nullable = false, length = 7)
    private String currencyPair;

    @Column(name = "rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "admin_user_id", nullable = false, length = 50)
    private String adminUserId;

    @Column(name = "override_timestamp", nullable = false)
    private LocalDateTime overrideTimestamp;

    // STORY: FPMAPP-9048 - Entity to store admin override records with user IDs and timestamps

    public CurrencyRateOverride() {
    }

    public CurrencyRateOverride(String currencyPair, BigDecimal rate, LocalDateTime effectiveDate, String adminUserId, LocalDateTime overrideTimestamp) {
        this.currencyPair = currencyPair;
        this.rate = rate;
        this.effectiveDate = effectiveDate;
        this.adminUserId = adminUserId;
        this.overrideTimestamp = overrideTimestamp;
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

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(String adminUserId) {
        this.adminUserId = adminUserId;
    }

    public LocalDateTime getOverrideTimestamp() {
        return overrideTimestamp;
    }

    public void setOverrideTimestamp(LocalDateTime overrideTimestamp) {
        this.overrideTimestamp = overrideTimestamp;
    }
}
