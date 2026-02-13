package com.moneyflow.account.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static CustomUserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal)) {
            return null;
        }
        return (CustomUserPrincipal) authentication.getPrincipal();
    }

    public static void checkRole(String requiredRole) {
        CustomUserPrincipal user = getCurrentUser();
        if (user == null || !requiredRole.equals(user.getRole())) {
            throw new RuntimeException("No permission");
        }
    }
}