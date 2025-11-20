"use client";

import { ReactNode, useEffect } from "react";

import { useAuthStore } from "@/lib/store";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { useRouter } from 'next/navigation';

interface AuthProviderProps {
    children: ReactNode;
}

/**
 * 🔐 AuthProvider
 *
 * Componente para gestionar el estado de la sesión del usuario a través de la app.
 * - Limpia la sesión si el token expira o el usuario no es válido.
 * - Limpia la cache de React Query al cerrar sesión.
 */
export function AuthProvider({ children }: AuthProviderProps) {
    const router = useRouter();
    const queryClient = useQueryClient();

    const { token, user, logout } = useAuthStore();

    // Limpiar cache de React Query si el token desaparece
    useEffect(() => {
        if (!token) {
            queryClient.clear();
        }
    }, [token, queryClient]);

    // Cerrar sesión si el token existe pero no hay datos de usuario (sesión corrupta/expirada)
    useEffect(() => {
        if (token && user === null) {
            toast.error("Tu sesión ha expirado. Por favor, inicia sesión de nuevo.");
            logout();
            router.push("/app/login");
        }
    }, [user, token, logout, router]);

    return <>{children}</>;
}