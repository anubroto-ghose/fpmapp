package com.fpm.model;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// STORY: FPMAPP-9037 - DTO for admin currency rate override request
public class CurrencyRateOverrideRequest {

    @NotBlank(message = "currencyPair must not be blank")
    private String currencyPair;

    @NotNull(message = "rate must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "rate must be positive")
    private BigDecimal rate;

    @NotBlank(message = "overrideReason must not be blank")
    private String overrideReason;

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