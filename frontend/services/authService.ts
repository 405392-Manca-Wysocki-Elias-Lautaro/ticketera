import api from '@/lib/api';
import { LoginRequest } from '@/types/AuthRequest';
import { AuthResponse } from '@/types/AuthResponse';

const BASE_URL = "/auth";

export const authService = {
    login: (credentials: LoginRequest) => api.post<AuthResponse>(`${BASE_URL}/login`, credentials),

    //TODO: Cambiar any por type ForgotPassword
    forgotPassword: (data: any) =>
        api.post<AuthResponse>(`${BASE_URL}/change-password`, data),
};
