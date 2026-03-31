package com.fpm.controller;

import com.fpm.dto.CurrencyOverrideDTO;
import com.fpm.service.CurrencyOverrideService;
import com.fpm.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currency-overrides")
public class CurrencyOverrideController {

    @Autowired
    private CurrencyOverrideService currencyOverrideService;

    // STORY: FPMAPP-9049 - Endpoint to get all current and historical currency overrides
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CurrencyOverrideDTO>> getAllOverrides() {
        List<CurrencyOverrideDTO> overrides = currencyOverrideService.getAllOverrides();
        return ResponseEntity.ok(overrides);
    }

    // STORY: FPMAPP-9049 - Endpoint to submit a new currency override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyOverrideDTO> createOverride(@RequestBody CurrencyOverrideDTO overrideDTO) {
        // TODO: Validate overrideDTO fields
        String currentUser = SecurityUtil.getCurrentUsername();
        CurrencyOverrideDTO created = currencyOverrideService.createOverride(overrideDTO, currentUser);
        return ResponseEntity.ok(created);
    }

    // STORY: FPMAPP-9049 - Endpoint to get override by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyOverrideDTO> getOverrideById(@PathVariable Long id) {
        CurrencyOverrideDTO overrideDTO = currencyOverrideService.getOverrideById(id);
        return ResponseEntity.ok(overrideDTO);
    }

}
