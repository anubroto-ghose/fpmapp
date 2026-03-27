package com.fpm.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    // STORY: FPMAPP-8761 - Utility method to check if current user has admin override permission
    public boolean hasAdminOverridePermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        // TODO: Implement actual permission check logic based on roles/authorities
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN_OVERRIDE".equals(grantedAuthority.getAuthority()));
    }
}