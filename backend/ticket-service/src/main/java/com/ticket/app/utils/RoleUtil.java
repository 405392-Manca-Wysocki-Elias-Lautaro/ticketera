package com.ticket.app.utils;

import com.ticket.app.domain.enums.UserRole;
import java.util.Set;

public final class RoleUtil {

    private static final Set<UserRole> PRIVILEGED_ROLES = Set.of(
            UserRole.ADMIN,
            UserRole.STAFF,
            UserRole.SUPER_ADMIN
    );

    // Constructor privado para evitar instanciación
    private RoleUtil() {}

    /**
     * Verifica si el rol coincide con uno específico.
     */
    public static boolean hasRole(UserRole actualRole, UserRole requiredRole) {
        return actualRole == requiredRole;
    }

    /**
     * Verifica si el rol coincide con alguno de los roles provistos.
     */
    public static boolean hasAnyRole(UserRole actualRole, UserRole... roles) {
        for (UserRole role : roles) {
            if (actualRole == role) return true;
        }
        return false;
    }

    /**
     * Verifica si el rol pertenece al conjunto de roles privilegiados.
     */
    public static boolean isPrivileged(UserRole actualRole) {
        return PRIVILEGED_ROLES.contains(actualRole);
    }
}
