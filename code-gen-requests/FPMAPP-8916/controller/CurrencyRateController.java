package com.fpm.controller;

import com.fpm.dto.CurrencyRateDto;
import com.fpm.service.CurrencyRateService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/currency-rates")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    public CurrencyRateController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping("/latest/{currencyCode}")
    public ResponseEntity<CurrencyRateDto> getLatestRate(@PathVariable @NotBlank String currencyCode) {
        // STORY: FPMAPP-8916 - API to get latest currency rate
        CurrencyRateDto dto = currencyRateService.getLatestRate(currencyCode.toUpperCase());
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/history/{currencyCode}")
    public ResponseEntity<List<CurrencyRateDto>> getHistoricalRates(
            @PathVariable @NotBlank String currencyCode,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        // STORY: FPMAPP-8916 - API to get historical currency rates
        List<CurrencyRateDto> rates = currencyRateService.getHistoricalRates(currencyCode.toUpperCase(), start, end);
        return ResponseEntity.ok(rates);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/override")
    public ResponseEntity<CurrencyRateDto> overrideCurrencyRate(@RequestBody OverrideRequest request) {
        // STORY: FPMAPP-8916 - API for admin override of currency rate
        CurrencyRateDto overridden = currencyRateService.overrideCurrencyRate(
                request.getCurrencyCode().toUpperCase(),
                request.getRateDate(),
                request.getOverrideBy(),
                request.getReason(),
                request.getNewRate());
        return ResponseEntity.ok(overridden);
    }

    public static class OverrideRequest {

        @NotBlank
        private String currencyCode;

        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime rateDate;

        @NotBlank
        private String overrideBy;

        @NotBlank
        private String reason;

        @NotNull
        private Double newRate;

        // Getters and setters

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

        public String getOverrideBy() {
            return overrideBy;
        }

        public void setOverrideBy(String overrideBy) {
            this.overrideBy = overrideBy;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Double getNewRate() {
            return newRate;
        }

        public void setNewRate(Double newRate) {
            this.newRate = newRate;
        }
    }

}
