package com.fpm.controller;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.dto.CurrencyOverrideResponse;
import com.fpm.exception.PermissionDeniedException;
import com.fpm.exception.InvalidCurrencyPairException;
import com.fpm.service.CurrencyOverrideService;
import com.fpm.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency-override")
public class CurrencyOverrideController {

    @Autowired
    private CurrencyOverrideService currencyOverrideService;

    // STORY: FPMAPP-8757 - Reject override submission by unauthorized user with invalid currency pair
    @PostMapping("/submit")
    public ResponseEntity<CurrencyOverrideResponse> submitOverride(@RequestBody CurrencyOverrideRequest request) {
        // Check user permissions
        if (!SecurityUtil.hasAdminOverridePermission()) {
            // TODO: Log attempt if needed (but per story, no override action logged)
            CurrencyOverrideResponse response = new CurrencyOverrideResponse();
            response.setSuccess(false);
            response.setMessage("Insufficient permissions to submit override.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Validate currency pair
        if (!currencyOverrideService.isValidCurrencyPair(request.getFromCurrency(), request.getToCurrency())) {
            CurrencyOverrideResponse response = new CurrencyOverrideResponse();
            response.setSuccess(false);
            response.setMessage("Invalid currency pair provided.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // If valid and authorized, proceed with override
        boolean applied = currencyOverrideService.applyOverride(request);
        CurrencyOverrideResponse response = new CurrencyOverrideResponse();
        response.setSuccess(applied);
        response.setMessage(applied ? "Override applied successfully." : "Failed to apply override.");
        return new ResponseEntity<>(response, applied ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}