import api from '@/lib/api';
import { ChangePasswordRequest } from '@/types/Request/ChangePasswordRequest';
import { LoginRequest } from '@/types/Request/LoginRequest';
import { RegisterRequest } from '@/types/Request/RegisterRequest';
import { ResetPasswordRequest } from '@/types/Request/ResetPasswordRequest';
import { ApiResponse } from '@/types/Response/ApiResponse';
import { AuthResponse } from '@/types/Response/AuthResponse';

const BASE_URL = "/auth";

export const authService = {
    login: (credentials: LoginRequest) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/login`, credentials
    ),

    logout: () => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/logout`
    ),

    register: (credentials: RegisterRequest) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/register`, credentials
    ),

    resendVerificationEmail: (email: string) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/resend-verification`, { email }
    ),

    verifyEmail: (token: string) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/verify`, null, { params: { token } }
    ),

    forgotPassword: (email: string) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/forgot-password`, { email }
    ),

    resetPassword: (data: ResetPasswordRequest) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/reset-password`, data
    ),

    changePassword: (data: ChangePasswordRequest) => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/change-password`, data
    ),

    me: () => api.get<ApiResponse<AuthResponse>>(
        `${BASE_URL}/me`
    ),

    refresh: () => api.post<ApiResponse<AuthResponse>>(
        `${BASE_URL}/refresh`
    ),
};
