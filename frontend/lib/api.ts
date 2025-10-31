import axios from "axios"
import { useAuthStore } from "@/lib/store"
import { handleApiError } from "@/utils/handleApiError"

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL,
    withCredentials: true,
})

// 🔸 Interceptor de request — añade el token automáticamente
api.interceptors.request.use((config) => {
    const token = useAuthStore.getState().token
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

// 🔸 Interceptor de respuesta — maneja expiración y errores globales
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config
        const status = error.response?.status
        const data = error.response?.data

        // 🧭 Manejo de expiración de token (401) con refresh automático
        if (status === 401 && !originalRequest._retry && data.data.code != "INVALID_CREDENTIALS") {
            originalRequest._retry = true
            try {
                const { data } = await axios.post(
                    `${process.env.NEXT_PUBLIC_API_URL}/auth/refresh`,
                    {},
                    { withCredentials: true }
                )

                const newAccessToken = data.accessToken
                useAuthStore.getState().setToken(newAccessToken)

                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
                return api(originalRequest)
            } catch (refreshError) {
                useAuthStore.getState().logout()
                if (typeof window !== "undefined") window.location.href = "/login"
                return Promise.reject(refreshError)
            }
        }

        handleApiError(error)

        return Promise.reject(error)
    }
)

export default api
