package com.fpm.controller;

import com.fpm.dto.CurrencyRateOverrideRequest;
import com.fpm.model.CurrencyRate;
import com.fpm.model.CurrencyRateOverride;
import com.fpm.repository.CurrencyRateOverrideRepository;
import com.fpm.repository.CurrencyRateRepository;
import com.fpm.service.CurrencyRateSyncService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fpm/currency/rates")
public class CurrencyConversionController {

    @Autowired
    private CurrencyRateSyncService currencyRateSyncService;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Autowired
    private CurrencyRateOverrideRepository currencyRateOverrideRepository;

    // STORY: FPMAPP-9048 - POST /sync endpoint to trigger synchronization from third-party provider
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> syncCurrencyRates() {
        try {
            currencyRateSyncService.syncRatesFromProvider();
            return ResponseEntity.ok("Currency rates synchronization triggered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to synchronize currency rates: " + e.getMessage());
        }
    }

    // STORY: FPMAPP-9048 - GET endpoint to retrieve current or historical currency exchange rates
    @GetMapping
    public ResponseEntity<?> getCurrencyRates(
            @RequestParam("currency_pair") String currencyPair,
            @RequestParam(value = "timestamp", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp) {

        if (timestamp == null) {
            // Return latest rate
            Optional<CurrencyRate> latestRate = currencyRateRepository.findTopByCurrencyPairOrderByRateTimestampDesc(currencyPair);
            if (latestRate.isPresent()) {
                return ResponseEntity.ok(latestRate.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No currency rate found for pair: " + currencyPair);
            }
        } else {
            // Return historical rate at or before timestamp
            List<CurrencyRate> rates = currencyRateRepository.findRatesByCurrencyPairAndTimestamp(currencyPair, timestamp);
            if (rates.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No currency rate found for pair: " + currencyPair + " at or before " + timestamp);
            } else {
                return ResponseEntity.ok(rates.get(0));
            }
        }
    }

    // STORY: FPMAPP-9048 - POST endpoint for admin override of currency rates with logging and email alert
    @PostMapping("/override")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> overrideCurrencyRate(@Valid @RequestBody CurrencyRateOverrideRequest request) {
        try {
            // Validate input
            if (request.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Rate must be positive.");
            }

            LocalDateTime effectiveDate = request.getEffectiveDate();
            if (effectiveDate == null) {
                return ResponseEntity.badRequest().body("Effective date is required.");
            }

            // Save override record
            CurrencyRateOverride override = new CurrencyRateOverride(
                    request.getCurrencyPair(),
                    request.getRate(),
                    effectiveDate,
                    request.getAdminUserId(),
                    LocalDateTime.now()
            );
            currencyRateOverrideRepository.save(override);

            // Insert or update currency_rates table with override flag
            CurrencyRate currencyRate = new CurrencyRate(
                    request.getCurrencyPair(),
                    request.getRate(),
                    effectiveDate,
                    true
            );
            currencyRateRepository.save(currencyRate);

            // Send email alert
            currencyRateSyncService.sendOverrideAlertEmail(override);

            return ResponseEntity.ok("Currency rate override saved and alert sent.");
        } catch (MessagingException mex) {
            // Email sending failed but override saved
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Override saved but failed to send alert email: " + mex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to override currency rate: " + e.getMessage());
        }
    }

}
