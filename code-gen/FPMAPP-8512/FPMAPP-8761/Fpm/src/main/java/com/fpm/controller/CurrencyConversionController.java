package com.fpm.controller;

import com.fpm.exception.AccessDeniedException;
import com.fpm.service.CurrencyOverrideService;
import com.fpm.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency-override")
public class CurrencyConversionController {

    @Autowired
    private CurrencyOverrideService currencyOverrideService;

    @Autowired
    private SecurityUtil securityUtil;

    // STORY: FPMAPP-8761 - Deny access to currency override interface for unauthorized user
    @GetMapping("/interface-access")
    public ResponseEntity<?> checkOverrideInterfaceAccess() {
        if (!securityUtil.hasAdminOverridePermission()) {
            // User is unauthorized to access override interface
            throw new AccessDeniedException("Access denied to currency override interface.");
        }
        // TODO: Return interface metadata or access confirmation for authorized users
        return ResponseEntity.ok().body("Access granted to currency override interface.");
    }

    // STORY: FPMAPP-8761 - Deny access to currency override interface for unauthorized user
    @PostMapping("/update")
    public ResponseEntity<?> updateCurrencyOverrideSettings() {
        if (!securityUtil.hasAdminOverridePermission()) {
            // User is unauthorized to update override settings
            throw new AccessDeniedException("Access denied to update currency override settings.");
        }
        // TODO: Implement update logic for authorized users
        return ResponseEntity.ok().body("Currency override settings updated successfully.");
    }
}