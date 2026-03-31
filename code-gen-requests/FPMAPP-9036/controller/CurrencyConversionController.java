package com.fpm.controller;

import com.fpm.dto.CurrencyRateOverrideRequest;
import com.fpm.dto.CurrencyRateResponse;
import com.fpm.service.CurrencyExchangeRateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/fpm/currency/rates")
public class CurrencyConversionController {

    private final CurrencyExchangeRateService currencyExchangeRateService;

    public CurrencyConversionController(CurrencyExchangeRateService currencyExchangeRateService) {
        this.currencyExchangeRateService = currencyExchangeRateService;
    }

    // STORY: FPMAPP-9036 - GET endpoint to provide current and historical currency exchange rates
    @GetMapping
    public ResponseEntity<List<CurrencyRateResponse>> getCurrencyRates(@RequestParam(required = false) String timestamp) {
        LocalDateTime parsedTimestamp = null;
        if (timestamp != null) {
            try {
                parsedTimestamp = LocalDateTime.parse(timestamp);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        List<CurrencyRateResponse> rates = currencyExchangeRateService.getCurrencyRates(parsedTimestamp);
        return ResponseEntity.ok(rates);
    }

    // STORY: FPMAPP-9036 - POST endpoint to override currency exchange rates
    @PostMapping("/override")
    public ResponseEntity<?> overrideCurrencyRate(@RequestBody CurrencyRateOverrideRequest request, HttpServletRequest httpRequest) {
        // TODO: Implement proper authentication and authorization check for admin role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        // TODO: Extract user id from authentication principal
        Integer userId = extractUserIdFromAuthentication(authentication);
        if (userId == null) {
            return ResponseEntity.status(403).build();
        }

        boolean alertSent = currencyExchangeRateService.overrideCurrencyRate(request, userId);

        return ResponseEntity.ok(new OverrideResponse(true, alertSent));
    }

    private Integer extractUserIdFromAuthentication(Authentication authentication) {
        // TODO: Implement extraction of user id from authentication principal
        // For now, return dummy user id
        return 1;
    }

    private static class OverrideResponse {
        private boolean success;
        private boolean alertSent;

        public OverrideResponse(boolean success, boolean alertSent) {
            this.success = success;
            this.alertSent = alertSent;
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isAlertSent() {
            return alertSent;
        }
    }

}
