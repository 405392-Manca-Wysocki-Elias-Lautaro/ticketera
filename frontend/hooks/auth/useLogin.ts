import { authService } from "@/services/authService";
import { useAuthStore } from "@/lib/store";
import type { LoginRequest } from "@/types/Request/LoginRequest";
import { AxiosError } from "axios";
import { toast } from "sonner";
import { useRouter } from "next/navigation";
import { AuthResponse } from "@/types/Response/AuthResponse";
import { useMutation } from "@tanstack/react-query";
import { useResendVerificationEmail } from "@/hooks/auth/useResendVerificationEmail";
import { handleApiError } from "@/utils/handleApiError";
import { ApiResponse } from '@/types/Response/ApiResponse';

export function useLogin() {
    const { setToken, setUser, setSessionFlag } = useAuthStore();
    const router = useRouter();
    const { resend } = useResendVerificationEmail();

    return useMutation<ApiResponse<AuthResponse>, AxiosError, LoginRequest>({
        mutationFn: async (credentials: LoginRequest) => {
            const response = await authService.login(credentials);
            return response.data;
        },
        onSuccess: (data: ApiResponse<AuthResponse>) => {
            const authResponse: AuthResponse = data?.data;

            setToken(authResponse.accessToken);
            setUser(authResponse.user);
            setSessionFlag(true);

            toast.success(`Bienvenido ${authResponse.user.firstName || ""}`);
            router.push("/dashboard");
        },
        onError: (error: AxiosError<any>) => {
            const code = error.response?.data?.data?.code;

            if (code === "ACCOUNT_NOT_VERIFIED") {
                const email =
                    error.response?.data?.data?.details?.email ||
                    error.response?.data?.data?.details?.user_email ||
                    error.response?.data?.data?.email;

                toast.warning(
                    "Tu cuenta aún no está verificada. Te reenviamos un correo de verificación."
                );

                if (email) resend(email);
                router.push("/verify-email");
                return;
            }

            handleApiError(error);
        },
    });
}
