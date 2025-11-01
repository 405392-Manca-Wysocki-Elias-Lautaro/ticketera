import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { authService } from "@/services/authService";
import { useAuthStore } from "@/lib/store";
import type { AxiosError } from "axios";

export function useLogout() {
    const router = useRouter();
    const { logout } = useAuthStore();

    return useMutation<void, AxiosError>({
        mutationFn: async () => {
            await authService.logout();
        },
        onSuccess: () => {
            logout();
            toast.success("Sesión cerrada correctamente");
            router.push("/login");
        },
        onError: () => {
            logout();
            toast.warning("Sesión cerrada localmente");
            router.push("/login");
        },
    });
}
