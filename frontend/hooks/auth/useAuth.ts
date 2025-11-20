import { useAuthStore } from "@/lib/store";
import { useEffect } from "react";
import { useLogout } from "./useLogout";
import { toast } from 'sonner';
import { useRouter } from 'next/navigation';

export function useAuth() {

    const router = useRouter();
    const { token, user, isLoading, setLoading, setSessionFlag, logout: localLogout } = useAuthStore();
    const { mutateAsync: serverLogout } = useLogout();

    async function logout() {
        toast.loading("Cerrando sesión...");
        try {
            await serverLogout(); // Logout al backend real
            toast.dismiss();
            toast.success("Sesión cerrada correctamente");
        } catch {
            setSessionFlag(false);

            try {
                await fetch("/api/auth/local-logout", { method: "POST" });
            } catch {
                console.warn("No se pudo limpiar la cookie refreshToken");
            }

            toast.dismiss();
            toast.warning("Sesión cerrada localmente");
        } finally {
            localLogout();
            router.push("/app/dashboard");
        }
    }

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
        logout,
    };
}
