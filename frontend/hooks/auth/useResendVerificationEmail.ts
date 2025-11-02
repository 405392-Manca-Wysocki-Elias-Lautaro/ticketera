import { useState, useEffect } from "react"
import { useMutation } from "@tanstack/react-query"
import { authService } from "@/services/authService"

export function useResendVerificationEmail(initialCooldown = 0) {
    const [cooldown, setCooldown] = useState(initialCooldown)

    const { mutateAsync: resend, isLoading, isSuccess, isError, error } = useMutation({
        mutationFn: async (email: string) => {
            await authService.resendVerificationEmail(email)
        },
        onSuccess: () => {
            setCooldown(30)
        },
    })

    useEffect(() => {
        if (cooldown <= 0) return
        const interval = setInterval(() => setCooldown((c) => c - 1), 1000)
        return () => clearInterval(interval)
    }, [cooldown])

    return {
        resend,
        isLoading,
        success: isSuccess,
        error: isError
            ? (error as any)?.response?.data?.message || "Error al reenviar correo"
            : null,
        cooldown,
        setCooldown,
    }
}
