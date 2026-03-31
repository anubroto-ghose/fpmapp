package com.fpm.dto;

// STORY: FPMAPP-9037 - DTO for response of currency rate override API
public class CurrencyRateOverrideResponse {
    private boolean success;
    private boolean alertSent;

    public CurrencyRateOverrideResponse() {
    }

    public CurrencyRateOverrideResponse(boolean success, boolean alertSent) {
        this.success = success;
        this.alertSent = alertSent;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isAlertSent() {
        return alertSent;
    }

    public void setAlertSent(boolean alertSent) {
        this.alertSent = alertSent;
    }
}