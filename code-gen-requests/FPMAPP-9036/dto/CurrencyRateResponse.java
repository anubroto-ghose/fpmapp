package com.fpm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrencyRateResponse {

    private String currencyPair;
    private BigDecimal rate;
    private LocalDateTime rateTimestamp;
    private boolean isOverride;
    private String overrideReason;

    // STORY: FPMAPP-9036 - DTO for currency rate response including override info

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
}
