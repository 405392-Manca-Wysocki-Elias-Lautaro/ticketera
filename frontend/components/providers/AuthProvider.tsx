"use client";

import { ReactNode, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuthStore } from "@/lib/store";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

interface AuthProviderProps {
    children: ReactNode;
    publicRoutes?: string[];
}

export function AuthProvider({
    children,
    publicRoutes = ["/login", "/register"],
}: AuthProviderProps) {
    const router = useRouter();
    const pathname = usePathname();
    const queryClient = useQueryClient();

    const { token, user, logout } = useAuthStore();

    useEffect(() => {
        const isPublic = publicRoutes.includes(pathname);
        if (!token && !isPublic) {
            router.push("/login");
        }
        if (token && pathname === "/login") {
            router.push("/dashboard");
        }
    }, [token, pathname, router, publicRoutes]);

    useEffect(() => {
        if (!token) queryClient.clear();
    }, [token, queryClient]);

    useEffect(() => {
        if (token && user === null) {
            toast.error("Tu sesión ha expirado. Inicia sesión nuevamente.");
            logout();
            router.push("/login");
        }
    }, [user, token, logout, router]);

    return <>{children}</>;
}
