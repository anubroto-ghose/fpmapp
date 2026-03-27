package com.fpm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CurrencyOverrideRequest {

    @NotBlank(message = "Source currency must not be blank")
    private String sourceCurrency;

    @NotBlank(message = "Target currency must not be blank")
    private String targetCurrency;

    @NotNull(message = "Override rate must not be null")
    private BigDecimal overrideRate;

    private String reason;

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getOverrideRate() {
        return overrideRate;
    }

    public void setOverrideRate(BigDecimal overrideRate) {
        this.overrideRate = overrideRate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}