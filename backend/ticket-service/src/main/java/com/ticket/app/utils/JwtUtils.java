package com.ticket.app.utils;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.ticket.app.exception.exceptions.InvalidJwtUserIdException;
import com.ticket.app.exception.exceptions.JwtClaimNotFoundException;
import com.ticket.app.exception.exceptions.JwtNotFoundException;

@Component
public class JwtUtils {

    /**
     * Retrieves the current JWT from the SecurityContextHolder.
     * @return Jwt object if authenticated, otherwise throws exception.
     */
    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        throw new JwtNotFoundException();
    }

    /**
     * Extracts the user ID (subject) from the current JWT.
     * @return UUID of the authenticated user.
     */
    public UUID getUserId() {
        Jwt jwt = getCurrentJwt();
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtUserIdException();
        }
    }

    /**
     * Extracts the user's email from the JWT.
     */
    public String getEmail() {
        Jwt jwt = getCurrentJwt();
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new JwtClaimNotFoundException("email");
        }
        return email;
    }

    /**
     * Extracts the user's role from the JWT.
     */
    public String getRole() {
        Jwt jwt = getCurrentJwt();
        String role = jwt.getClaimAsString("role");
        if (role == null || role.isBlank()) {
            throw new JwtClaimNotFoundException("role");
        }
        return role;
    }

    /**
     * Extracts the deviceId if present in the JWT.
     */
    public UUID getDeviceId() {
        Jwt jwt = getCurrentJwt();
        String deviceId = jwt.getClaimAsString("deviceId");
        return (deviceId != null && !deviceId.isBlank())
                ? UUID.fromString(deviceId)
                : null;
    }
}
