package com.fpm.controller;

import com.fpm.model.CurrencyRate;
import com.fpm.service.CurrencyRateService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/currency-rates")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    public CurrencyRateController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    // STORY: FPMAPP-8916 - API to get latest currency rate for a given currency code
    @GetMapping("/latest/{currencyCode}")
    public ResponseEntity<CurrencyRate> getLatestRate(@PathVariable @NotBlank String currencyCode) {
        CurrencyRate rate = currencyRateService.getLatestRate(currencyCode.toUpperCase());
        if (rate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rate);
    }

    // STORY: FPMAPP-8916 - API to get historical currency rates between start and end dates
    @GetMapping("/history/{currencyCode}")
    public ResponseEntity<List<CurrencyRate>> getHistoricalRates(
            @PathVariable @NotBlank String currencyCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime endDate) {
        List<CurrencyRate> rates = currencyRateService.getHistoricalRates(currencyCode.toUpperCase(), startDate, endDate);
        return ResponseEntity.ok(rates);
    }

    // STORY: FPMAPP-8916 - Admin API to override currency rate with audit logging and alerting
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/override")
    public ResponseEntity<CurrencyRate> overrideCurrencyRate(
            @RequestParam @NotBlank String currencyCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @NotNull LocalDateTime rateDate,
            @RequestParam @NotNull BigDecimal newRate,
            @RequestParam @NotBlank String overrideBy,
            @RequestParam String reason) {
        CurrencyRate overriddenRate = currencyRateService.overrideCurrencyRate(currencyCode.toUpperCase(), rateDate, newRate, overrideBy, reason);
        return new ResponseEntity<>(overriddenRate, HttpStatus.OK);
    }
}
