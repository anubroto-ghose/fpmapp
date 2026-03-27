package com.fpm.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CurrencyOverrideRequest {

    @NotBlank(message = "Source currency must not be blank")
    private String sourceCurrency;

    @NotBlank(message = "Target currency must not be blank")
    private String targetCurrency;

    @NotNull(message = "Override rate must not be null")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Override rate must be positive")
    private BigDecimal overrideRate;

    @NotBlank(message = "Reason must not be blank")
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