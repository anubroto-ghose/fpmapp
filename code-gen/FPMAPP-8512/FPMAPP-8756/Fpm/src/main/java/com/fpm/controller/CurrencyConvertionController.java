package com.fpm.controller;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.exception.PermissionDeniedException;
import com.fpm.service.CurrencyOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/currency")
public class CurrencyConvertionController {

    @Autowired
    private CurrencyOverrideService currencyOverrideService;

    // STORY: FPMAPP-8756 - Reject override submission by unauthorized user with valid data
    @PostMapping("/override")
    public ResponseEntity<?> submitCurrencyOverride(@Valid @RequestBody CurrencyOverrideRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean hasOverridePermission = currencyOverrideService.hasOverridePermission(username);
        if (!hasOverridePermission) {
            // Reject override request due to insufficient permissions
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Error: Insufficient permissions to submit currency override.");
        }

        // TODO: Implement actual override submission logic if authorized
        currencyOverrideService.applyOverride(request, username);

        return ResponseEntity.ok("Override submitted successfully.");
    }
}