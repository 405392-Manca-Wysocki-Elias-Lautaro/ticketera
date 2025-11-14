import axios from "axios"
import { useAuthStore } from "@/lib/store"
import { handleApiError } from "@/utils/handleApiError"
import { authService } from '@/services/authService'

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL,
    withCredentials: true,
})

// 🔸 Interceptor de request — añade el token automáticamente
api.interceptors.request.use((config) => {
    const token = useAuthStore.getState().token;
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

// 🔸 Interceptor de respuesta — maneja expiración y errores globales
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        const status = error.response?.status;
        const data = error.response?.data;

        if (originalRequest.url.includes("/auth/refresh")) {
            useAuthStore.getState().logout();
            if (typeof window !== "undefined") window.location.href = "/login";
            return Promise.reject(error);
        }

        originalRequest._retryCount = originalRequest._retryCount || 0;
        const MAX_RETRIES = 1;

        if (
            status === 401 &&
            data?.data?.code !== "INVALID_CREDENTIALS" &&
            originalRequest._retryCount < MAX_RETRIES
        ) {
            originalRequest._retryCount++;

            try {
                const { data } = await authService.refresh();
                const newAccessToken = data?.data?.accessToken;

                useAuthStore.getState().setToken(newAccessToken);
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

                return api(originalRequest);

            } catch (err) {
                useAuthStore.getState().logout();
                if (typeof window !== "undefined") window.location.href = "/login";
                return Promise.reject(err);
            }
        }

        handleApiError(error);
        return Promise.reject(error);
    }
);


export default api
