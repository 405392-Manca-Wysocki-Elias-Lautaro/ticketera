"use client";

import { ReactNode, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuthStore } from "@/lib/store";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

interface AuthProviderProps {
    children: ReactNode;
}

/**
 * 🔐 AuthProvider
 *
 * Controla la sesión y la navegación según el estado del usuario.
 * - Permite libre acceso a todo lo que esté bajo /app/(public)
 * - Protege automáticamente /app/(protected)
 * - Muestra mensaje y limpia sesión si el token expira o el usuario no es válido.
 */
export function AuthProvider({ children }: AuthProviderProps) {
    const router = useRouter();
    const pathname = usePathname();
    const queryClient = useQueryClient();

    const { token, user, logout } = useAuthStore();

    const isPublicRoute = pathname.startsWith("/app/(public)") ||
        pathname === "/login" ||
        pathname === "/register";

    // 🚦 Control de acceso
    useEffect(() => {

        if (!token && !isPublicRoute) {
            router.push("/login");
            return;
        }

        if (token && (pathname === "/login" || pathname === "/register")) {
            router.push("/dashboard");
            return;
        }
    }, [token, pathname, router, isPublicRoute]);

    useEffect(() => {
        if (!token) queryClient.clear();
    }, [token, queryClient]);

    useEffect(() => {
        if (token && user === null) {
            toast.error("Your session has expired. Please log in again.");
            logout();
            router.push("/login");
        }
    }, [user, token, logout, router]);

    return <>{children}</>;
}
