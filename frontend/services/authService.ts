import api from '@/lib/api';
import { ChangePasswordRequest } from '@/types/Request/ChangePasswordRequest';
import { ForgotPasswordRequest } from '@/types/Request/ForgotPasswordRequest';
import { LoginRequest } from '@/types/Request/LoginRequest';
import { RegisterRequest } from '@/types/Request/RegisterRequest';
import { ResetPasswordRequest } from '@/types/Request/ResetPasswordRequest';
import { ApiResponse } from '@/types/Response/Apiresponse';

const BASE_URL = "/auth";

export const authService = {
    login: (credentials: LoginRequest) => api.post<ApiResponse>(
        `${BASE_URL}/login`, credentials
    ),

    logout: () => api.post<ApiResponse>(
        `${BASE_URL}/logout`, {}
    ),

    register: (credentials: RegisterRequest) => api.post<ApiResponse>(
        `${BASE_URL}/register`, credentials
    ),

    resendVerificationEmail: (email: string) => api.post<ApiResponse>(
        `${BASE_URL}/resend-verification`, { email }
    ),

    verifyEmail: (token: string) => api.post<ApiResponse>(
        `${BASE_URL}/verify`, null, { params: { token } }
    ),


    forgotPassword: (email: string) => api.post<ApiResponse>(
        `${BASE_URL}/forgot-password`, { email }
    ),

    resetPassword: (data: ResetPasswordRequest) => api.post<ApiResponse>(
        `${BASE_URL}/reset-password`, data
    ),

    changePassword: (data: ChangePasswordRequest) => api.post<ApiResponse>(
        `${BASE_URL}/change-password`, data
    ),

    me: () => api.get<ApiResponse>(
        `${BASE_URL}/me`
    ),

};
