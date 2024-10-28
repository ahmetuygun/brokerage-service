package com.ing.brokerage.common;

import com.ing.brokerage.auth.model.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static CustomUserDetails getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }

        return null; //
    }
}
