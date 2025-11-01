// src/hooks/auth/useLogin.ts
import { authService } from "@/services/authService";
import { useAuthStore } from "@/lib/store";
import { useMutation } from "@tanstack/react-query";
import type { AuthResponse } from "@/types/AuthResponse";
import type { LoginRequest } from "@/types/AuthRequest";
import { AxiosError } from "axios";
import { toast } from "sonner";
import { useRouter } from "next/navigation";
import { ApiResponse } from '@/types/Apiresponse';

export function useLogin() {
    const { setToken, setUser } = useAuthStore();
    const router = useRouter();

    return useMutation<ApiResponse, AxiosError, LoginRequest>({
        mutationFn: async (credentials) => {
            const response = await authService.login(credentials);
            return response.data;
        },
        onSuccess: (data) => {
            const authResponse :AuthResponse = data?.data;

            setToken(authResponse.accessToken);
            setUser(authResponse.user);

            toast.success(`Bienvenido ${authResponse.user.firstName || ""}`);
            router.push("/dashboard");
        }
    });
}
