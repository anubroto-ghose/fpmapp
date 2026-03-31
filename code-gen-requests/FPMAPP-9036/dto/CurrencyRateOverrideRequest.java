package com.fpm.dto;

import java.math.BigDecimal;

public class CurrencyRateOverrideRequest {

    private String currencyPair;
    private BigDecimal rate;
    private String overrideReason;

    // STORY: FPMAPP-9036 - DTO for override request

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

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }
}
