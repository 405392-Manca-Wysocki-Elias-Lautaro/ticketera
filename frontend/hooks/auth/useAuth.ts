import { useAuthStore } from '@/lib/store';
import { useCallback, useEffect } from "react";

export function useAuth() {
    const { token, user, isLoading, setToken, setUser, logout, setLoading } = useAuthStore();

    useEffect(() => {
        if (isLoading) {
            // opcional: podrías hacer un fetch /me aquí si querés validar token
            setLoading(false);
        }
    }, [isLoading, setLoading]);

    const login = useCallback(async (email: string, password: string) => {
        setLoading(true);
        try {
            const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/auth/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
            });

            if (!res.ok) throw new Error("Login failed");

            const data = await res.json();

            setToken(data.access_token);
            setUser(data.user);
        } finally {
            setLoading(false);
        }
    }, [setToken, setUser, setLoading]);

    const logoutUser = useCallback(() => {
        logout();
    }, [logout]);

    return {
        token,
        user,
        isLoading,
        isAuthenticated: !!token,
        login,
        logout: logoutUser,
    };
}
