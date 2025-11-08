import { useMutation } from "@tanstack/react-query"
import { authService } from "@/services/authService"

export function useVerifyEmail() {
    const {
        mutateAsync: verify,
        isLoading,
        isSuccess,
        isError,
        error,
    } = useMutation({
        mutationFn: async (token: string) => {
            if (!token) throw new Error("Token inválido o ausente.")
            await authService.verifyEmail(token)
        },
    })

    return {
        verify,
        isLoading,
        success: isSuccess,
        error: isError
            ? (error as any)?.response?.data?.message || "Error al verificar el correo"
            : null,
    }
}
