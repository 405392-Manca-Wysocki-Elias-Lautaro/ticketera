import { RoleCode } from "@/types/enums/RoleCode"

/**
 * Verifica si el usuario tiene uno de los roles permitidos.
 * El SUPER_ADMIN siempre devuelve true.
 */
export function hasRole(
    user: { role?: { code?: RoleCode } } | null | undefined,
    ...allowedRoles: RoleCode[]
): boolean {
    if (!user?.role?.code) return false

    if (user.role.code === RoleCode.SUPER_ADMIN) return true

    return allowedRoles.includes(user.role.code)
}

/**
 * Utilidades específicas por rol o permiso lógico.
 */
export const RoleUtils = {
    isAdmin: (user?: { role?: { code?: RoleCode } } | null) =>
        hasRole(user, RoleCode.ADMIN),

    isStaff: (user?: { role?: { code?: RoleCode } } | null) =>
        hasRole(user, RoleCode.STAFF),

    isCustomer: (user?: { role?: { code?: RoleCode } } | null) =>
        hasRole(user, RoleCode.CUSTOMER),

    isSuperAdmin: (user?: { role?: { code?: RoleCode } } | null) =>
        hasRole(user, RoleCode.SUPER_ADMIN),

    canManageEvents: (user?: { role?: { code?: RoleCode } } | null) =>
        hasRole(user, RoleCode.ADMIN, RoleCode.STAFF),

    canManageEverything: (user?: { role?: { code?: RoleCode } } | null) =>
        hasRole(user, RoleCode.SUPER_ADMIN),
}
