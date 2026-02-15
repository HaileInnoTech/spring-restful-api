package com.haile.springrestfulapi.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class SecurityUtil {

    public static Optional<String> getCurrentUsernameLogin() {
        Authentication authentication = getAuthentication();
        return Optional.ofNullable(extractUsername(authentication));
    }

    public static Optional<Long> getCurrentIdLogin() {
        Authentication authentication = getAuthentication();
        return extractUserId(authentication);
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext()
                .getAuthentication();
    }

    private static String extractUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        }

        if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        }

        if (principal instanceof String s) {
            return s;
        }

        return null;
    }

    private static Optional<Long> extractUserId(Authentication authentication) {
        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String idClaim = jwt.getClaimAsString("id");
            if (idClaim == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(Long.parseLong(idClaim));
            }
            catch (NumberFormatException ex) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}