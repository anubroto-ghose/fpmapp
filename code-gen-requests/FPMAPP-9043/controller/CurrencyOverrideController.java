package com.fpm.controller;

import com.fpm.dto.CurrencyOverrideRequest;
import com.fpm.dto.CurrencyOverrideAuditLogDTO;
import com.fpm.service.CurrencyOverrideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpm/currency-rates")
public class CurrencyOverrideController {

    @Autowired
    private CurrencyOverrideService currencyOverrideService;

    // STORY: FPMAPP-9043 - Submit currency rate override from admin UI
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/override")
    public ResponseEntity<?> submitOverride(@RequestBody CurrencyOverrideRequest request) {
        // TODO: Validate request fields
        var response = currencyOverrideService.submitOverride(request);
        return ResponseEntity.ok(response);
    }

    // STORY: FPMAPP-9043 - Fetch audit logs of currency rate overrides for display
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/override-logs")
    public ResponseEntity<List<CurrencyOverrideAuditLogDTO>> getOverrideLogs() {
        List<CurrencyOverrideAuditLogDTO> logs = currencyOverrideService.getOverrideAuditLogs();
        return ResponseEntity.ok(logs);
    }
}
