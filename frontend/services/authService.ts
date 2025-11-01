import api from '@/lib/api';
import { ApiResponse } from '@/types/Apiresponse';
import { LoginRequest } from '@/types/AuthRequest';

const BASE_URL = "/auth";

export const authService = {
    login: (credentials: LoginRequest) => api.post<ApiResponse>(
        `${BASE_URL}/login`, credentials, {withCredentials: true}
    ),

    logout: () => api.post<ApiResponse>(
        `${BASE_URL}/logout`, {}, {withCredentials: true}
    ),

    //TODO: Cambiar any por type ForgotPassword
    forgotPassword: (data: any) => api.post<ApiResponse>(
        `${BASE_URL}/change-password`, data
    ),
};
