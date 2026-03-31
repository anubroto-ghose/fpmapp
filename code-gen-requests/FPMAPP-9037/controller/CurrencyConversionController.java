package com.fpm.controller;

import com.fpm.dto.CurrencyRateOverrideResponse;
import com.fpm.model.CurrencyRateOverrideRequest;
import com.fpm.service.CurrencyRateOverrideService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// STORY: FPMAPP-9037 - Add API endpoint for admin currency rate override
@RestController
@RequestMapping("/fpm/currency/rates")
public class CurrencyConversionController {

    @Autowired
    private CurrencyRateOverrideService currencyRateOverrideService;

    // STORY: FPMAPP-9037 - POST endpoint for admin override of currency rates
    @PostMapping("/override")
    public ResponseEntity<CurrencyRateOverrideResponse> overrideCurrencyRate(
            @Valid @RequestBody CurrencyRateOverrideRequest request,
            Authentication authentication) {
        // TODO: Validate that authenticated user has admin privileges
        String userId = authentication.getName();

        boolean alertSent = currencyRateOverrideService.overrideCurrencyRate(
                request.getCurrencyPair(),
                request.getRate(),
                request.getOverrideReason(),
                userId);

        CurrencyRateOverrideResponse response = new CurrencyRateOverrideResponse(true, alertSent);
        return ResponseEntity.ok(response);
    }
}