package com.fpm.controller;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.dto.ErrorResponse;
import com.fpm.service.CurrencyConversionService;
import com.fpm.exception.PermissionDeniedException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
public class CurrencyConversionController {

    @Autowired
    private CurrencyConversionService currencyConversionService;

    // STORY: FPMAPP-8758 - Reject override submission by unauthorized user with missing reason
    @PostMapping("/override")
    public ResponseEntity<?> submitCurrencyOverride(@Valid @RequestBody CurrencyOverrideRequest request, Authentication authentication) {
        // Check if user has admin override permission
        boolean hasOverridePermission = currencyConversionService.hasOverridePermission(authentication.getName());

        if (!hasOverridePermission) {
            // Reject if reason is missing
            if (request.getReason() == null || request.getReason().trim().isEmpty()) {
                // Return error about missing reason and insufficient permissions
                ErrorResponse error = new ErrorResponse();
                error.setMessage("Insufficient permissions and missing reason for override submission.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            // Reject override due to insufficient permissions
            ErrorResponse error = new ErrorResponse();
            error.setMessage("Insufficient permissions to submit override.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        // If user has permission, but reason is missing, reject
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage("Override reason is required.");
            return ResponseEntity.badRequest().body(error);
        }

        // Proceed with override submission
        try {
            currencyConversionService.applyOverride(request, authentication.getName());
            return ResponseEntity.ok().build();
        } catch (PermissionDeniedException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage("Failed to apply override.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}