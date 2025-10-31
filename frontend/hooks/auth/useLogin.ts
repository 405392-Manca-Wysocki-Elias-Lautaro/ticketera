// src/hooks/auth/useLogin.ts
import { authService } from "@/services/authService";
import { useAuthStore } from "@/lib/store";
import { useMutation } from "@tanstack/react-query";
import type { AuthResponse } from "@/types/AuthResponse";
import type { LoginRequest } from "@/types/AuthRequest";
import { AxiosError } from "axios";
import { toast } from "sonner";
import { useRouter } from "next/navigation";

export function useLogin() {
    const { setToken, setUser } = useAuthStore();
    const router = useRouter();

    return useMutation<AuthResponse, AxiosError, LoginRequest>({
        mutationFn: async (credentials) => {
            const response = await authService.login(credentials);
            return response.data;
        },
        onSuccess: (data) => {
            setToken(data.accessToken);
            setUser(data.user);

            toast.success(`Bienvenido ${data.user.firstName || ""}`);
            router.push("/dashboard");
        }
    });
}
