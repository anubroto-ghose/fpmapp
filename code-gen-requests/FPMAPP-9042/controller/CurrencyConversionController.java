package com.fpm.controller;

import com.fpm.dto.CurrencyRateDTO;
import com.fpm.dto.CurrencyRateOverrideRequest;
import com.fpm.service.CurrencyRateService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/fpm/currency-rates")
public class CurrencyConversionController {

    private final CurrencyRateService currencyRateService;

    public CurrencyConversionController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    // STORY: FPMAPP-9042 - Fetch current or historical currency exchange rates with optional timestamp filter
    @GetMapping
    public ResponseEntity<CurrencyRateDTO> getCurrencyRate(
            @RequestParam("currency_pair") String currencyPair,
            @RequestParam(value = "timestamp", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {
        CurrencyRateDTO rateDTO = currencyRateService.getCurrencyRate(currencyPair, timestamp);
        if (rateDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rateDTO);
    }

    // STORY: FPMAPP-9042 - Admin endpoint to override currency exchange rates with reason and effective date
    @PostMapping("/override")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyRateDTO> overrideCurrencyRate(@Valid @RequestBody CurrencyRateOverrideRequest request) {
        CurrencyRateDTO overriddenRate = currencyRateService.overrideCurrencyRate(request);
        return ResponseEntity.ok(overriddenRate);
    }

}
