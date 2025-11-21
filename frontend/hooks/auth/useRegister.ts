import { authService } from "@/services/authService";
import { useMutation } from "@tanstack/react-query"
import { AxiosError } from "axios";
import { useRouter } from "next/navigation";
import { ApiResponse } from '@/types/Response/ApiResponse';
import { RegisterRequest } from '@/types/Request/RegisterRequest';
import { AuthResponse } from '@/types/Response/AuthResponse';

export function useRegister() {
    const router = useRouter();

    return useMutation<ApiResponse<AuthResponse>, AxiosError, RegisterRequest>({
        mutationFn: async (credentials: RegisterRequest) => {
            const response = await authService.register(credentials);
            return response.data;
        },
        onSuccess: (response: ApiResponse<AuthResponse>) => {
            const authResponse: AuthResponse = response.data;
            router.push(`/verify-email?email=${encodeURIComponent(authResponse.user.email)}`)
        }
    });
}
