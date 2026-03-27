package com.fpm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/currency-override")
public class CurrencyConversionController {

    // STORY: FPMAPP-8759 - Deny access to currency override interface for unauthorized user
    @GetMapping("/interface")
    public ResponseEntity<String> getCurrencyOverrideInterface(Authentication authentication) {
        if (!hasAdminOverridePermission(authentication.getAuthorities())) {
            // STORY: FPMAPP-8759 - Return 403 Forbidden with error message for unauthorized access
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: insufficient permissions to access currency override interface.");
        }
        // TODO: Return the actual currency override interface data or view
        return ResponseEntity.ok("Currency override interface data");
    }

    private boolean hasAdminOverridePermission(Collection<? extends GrantedAuthority> authorities) {
        // TODO: Replace "ROLE_ADMIN_OVERRIDE" with actual permission/role name used in the system
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN_OVERRIDE"));
    }
}