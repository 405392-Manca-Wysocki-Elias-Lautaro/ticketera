import { authService } from "@/services/authService";
import { useMutation } from "@tanstack/react-query"
import { AxiosError } from "axios";
import { ApiResponse } from '@/types/Response/ApiResponse';
import { AuthResponse } from '@/types/Response/AuthResponse';
import { ForgotPasswordRequest } from '@/types/Request/ForgotPasswordRequest';

export function useForgotPassword() {
    return useMutation<ApiResponse<AuthResponse>, AxiosError, ForgotPasswordRequest>({
        mutationFn: async (forgotPasswordRequest: ForgotPasswordRequest) => {
            const response = await authService.forgotPassword(forgotPasswordRequest);
            return response.data;
        },
    });
}
