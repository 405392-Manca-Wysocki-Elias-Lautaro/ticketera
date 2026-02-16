package com.order.app.utils;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.order.app.exceptions.InvalidJwtUserIdException;
import com.order.app.exceptions.JwtClaimNotFoundException;
import com.order.app.exceptions.JwtNotFoundException;

/**
 * Utilidad para extraer información del JWT del usuario autenticado.
 * Usado principalmente para endpoints de cupones que requieren identificar al organizador.
 */
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
     * Extracts the organizerId if present in the JWT.
     * For SUPER_ADMIN and ADMIN: if organizerId is not present, uses userId as fallback.
     * For OWNER: organizerId is required in the JWT.
     */
    public UUID getOrganizerId() {
        Jwt jwt = getCurrentJwt();
        String role = getRole();
        String organizerId = jwt.getClaimAsString("organizerId");
        
        // Si hay organizerId en el JWT, lo usamos
        if (organizerId != null && !organizerId.isBlank()) {
            try {
                return UUID.fromString(organizerId);
            } catch (IllegalArgumentException e) {
                throw new InvalidJwtUserIdException();
            }
        }
        
        // Si no hay organizerId, verificamos el rol
        if ("SUPER_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            // Para SUPER_ADMIN y ADMIN, usamos el userId como organizerId
            try {
                return getUserId();
            } catch (Exception e) {
                throw new JwtClaimNotFoundException("No se pudo obtener organizerId ni userId del JWT");
            }
        }
        
        // Para OWNER y otros roles, el organizerId es requerido
        if ("OWNER".equalsIgnoreCase(role)) {
            throw new JwtClaimNotFoundException("organizerId. El JWT debe incluir el claim 'organizerId' para el rol OWNER.");
        }
        
        throw new JwtClaimNotFoundException("organizerId");
    }

    /**
     * Checks if the user has OWNER role.
     */
    public boolean isOwner() {
        try {
            return "OWNER".equalsIgnoreCase(getRole());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the user has ADMIN role.
     */
    public boolean isAdmin() {
        try {
            String role = getRole();
            return "ADMIN".equalsIgnoreCase(role) || "OWNER".equalsIgnoreCase(role);
        } catch (Exception e) {
            return false;
        }
    }
}

