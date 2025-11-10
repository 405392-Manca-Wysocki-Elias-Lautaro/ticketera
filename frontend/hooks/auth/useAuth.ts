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
            await serverLogout();
            toast.dismiss();
            toast.success("Sesión cerrada correctamente");
        } catch {
            setSessionFlag(false);
            toast.dismiss();
            toast.warning("Sesión cerrada localmente");
        } finally {
            localLogout();
            router.push("/login");
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
