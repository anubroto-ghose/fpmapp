package com.fpm.dto;

import java.time.LocalDateTime;

public class CurrencyOverrideDTO {
    private Long id;
    private String currencyCode;
    private Double overriddenRate;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private String status; // e.g. ACTIVE, EXPIRED
    private String createdBy;
    private LocalDateTime createdAt;

    // STORY: FPMAPP-9049 - DTO for currency override data transfer

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

    public Double getOverriddenRate() {
        return overriddenRate;
    }

    public void setOverriddenRate(Double overriddenRate) {
        this.overriddenRate = overriddenRate;
    }

    public LocalDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(LocalDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
