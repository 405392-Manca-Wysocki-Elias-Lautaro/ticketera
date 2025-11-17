import { authService } from "@/services/authService";
import { useMutation } from "@tanstack/react-query"
import { AxiosError } from "axios";
import { ApiResponse } from '@/types/Response/ApiResponse';
import { AuthResponse } from '@/types/Response/AuthResponse';
import { ResetPasswordRequest } from '@/types/Request/ResetPasswordRequest';

export function useResetPassword() {
    return useMutation<ApiResponse<AuthResponse>, AxiosError, ResetPasswordRequest>({
        mutationFn: async (credentials: ResetPasswordRequest) => {
            const response = await authService.resetPassword(credentials);
            return response.data;
        }
    });
}
