package com.fpm.controller;

import com.fpm.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency-override")
public class CurrencyConversionController {

    // STORY: FPMAPP-8760 - Deny access to currency override interface for unauthorized user
    @GetMapping("/interface")
    public String accessCurrencyOverrideInterface() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // TODO: Replace with actual permission check logic for admin override permission
        boolean hasAdminOverridePermission = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN_OVERRIDE"));

        if (!hasAdminOverridePermission) {
            throw new AccessDeniedException();
        }

        // TODO: Return actual interface data or view
        return "Currency Override Interface Access Granted";
    }
}
