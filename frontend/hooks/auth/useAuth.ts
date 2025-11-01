import { useAuthStore } from "@/lib/store";
import { useEffect } from "react";
import { useLogout } from "./useLogout";

export function useAuth() {
    const { token, user, isLoading, setLoading } = useAuthStore();
    const { mutate: logout } = useLogout();

    useEffect(() => {
        if (isLoading) {
            setLoading(false);
        }
    }, [isLoading, setLoading]);

    return {
        token,
        user,
        isLoading,
        isAuthenticated: !!token,
        logout, // 🔥 logout por backend
    };
}
