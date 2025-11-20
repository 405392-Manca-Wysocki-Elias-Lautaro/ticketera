"use client"

import { useState, useEffect } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import Image from "next/image"
import Link from "next/link"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"
import { ArrowLeft, CheckCircle, Eye, EyeOff } from "lucide-react"
import GradientText from "@/components/GradientText"
import StarBorder from "@/components/StarBorder"

import { useResetPassword } from '@/hooks/auth/useResetPassword'
import { ResetPasswordSchema, resetPasswordSchema } from '@/schemas/auth/ResetPasswordSchema'
import PasswordStrengthIndicator from '@/components/auth/PasswordStrengthIndicator'


export default function ResetPasswordPage() {
    const router = useRouter();
    const searchParams = useSearchParams()
    const token = searchParams.get("token")
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);

    const [success, setSuccess] = useState(false)
    const [countdown, setCountdown] = useState(10)

    const { mutateAsync: resetPassword, isPending } = useResetPassword()

    const form = useForm<ResetPasswordSchema>({
        resolver: zodResolver(resetPasswordSchema),
        defaultValues: {
            token: token || "",
            password: "",
            confirmPassword: "",
        },
    });

    const { watch } = form

    // Countdown redirect
    useEffect(() => {
        if (!success) return

        if (countdown === 0) {
            router.push("/app/login?reset=true")
            return
        }

        const timer = setTimeout(() => setCountdown(prev => prev - 1), 1000)
        return () => clearTimeout(timer)

    }, [success, countdown])

    const onSubmit = async (data: ResetPasswordSchema) => {
        try {
            await resetPassword({
                token: data.token,
                password: data.password,
                confirmPassword: data.confirmPassword,
            });

            setSuccess(true)
        } catch (err: any) {
            const message =
                err?.response?.data?.message ||
                "Error al restablecer la contraseña. Intenta nuevamente."

            form.setError("password", { message })
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">

            {/* SUCCESS MESSAGE */}
            {success && (
                <Card className="w-full max-w-md">
                    <CardHeader className="text-center space-y-4">
                        <CheckCircle className="mx-auto h-12 w-12 text-green-600" />
                        <GradientText>
                            <CardTitle className="text-2xl">¡Contraseña Restablecida!</CardTitle>
                        </GradientText>
                        <CardDescription>
                            Serás redirigido al login en <strong>{countdown}</strong> segundos.
                        </CardDescription>
                    </CardHeader>

                    <CardContent className="space-y-4 text-center">
                        <StarBorder className="w-full">
                            <Button
                                onClick={() => router.push("/app/login")}
                                className="w-full gradient-brand text-white cursor-pointer"
                            >
                                Ir al Login Ahora
                            </Button>
                        </StarBorder>
                    </CardContent>
                </Card>
            )}

            {/* RESET PASSWORD FORM */}
            {!success && (
                <Card className="w-full max-w-md">
                    <CardHeader className="space-y-4">
                        <Button variant="ghost" asChild className="w-fit cursor-pointer">
                            <Link href="/app/login">
                                <ArrowLeft className="mr-2 h-4 w-4" />
                                Volver al login
                            </Link>
                        </Button>

                        <div className="flex justify-center">
                            <div className="relative h-16 w-16">
                                <Image src="/logo.png" alt="Ticketly" fill className="object-contain" />
                            </div>
                        </div>

                        <div className="text-center">
                            <GradientText>
                                <CardTitle className="text-2xl">Nueva Contraseña</CardTitle>
                            </GradientText>
                            <CardDescription>Ingresa tu nueva contraseña</CardDescription>
                        </div>
                    </CardHeader>

                    <CardContent>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">

                            {/* PASSWORD */}
                            <div className="space-y-2">
                                <Label htmlFor="password">Nueva Contraseña</Label>
                                <div className='relative'>
                                    <Input
                                        id="password"
                                        type={showPassword ? "text" : "password"}
                                        placeholder="••••••••"
                                        {...form.register("password")}
                                    />
                                    <button
                                        type="button"
                                        className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                                        onClick={() => setShowPassword(!showPassword)}
                                    >
                                        {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                    </button>
                                </div>
                                {form.formState.errors.password && (
                                    <Alert variant="destructive">
                                        <AlertDescription>
                                            {form.formState.errors.password.message}
                                        </AlertDescription>
                                    </Alert>
                                )}
                                <PasswordStrengthIndicator password={watch("password")} />

                            </div>

                            {/* CONFIRM PASSWORD */}
                            <div className="space-y-2">
                                <Label htmlFor="confirmPassword">Confirmar Contraseña</Label>
                                <div className="relative">
                                    <Input
                                        id="confirmPassword"
                                        type={showConfirm ? "text" : "password"}
                                        placeholder="••••••••"
                                        {...form.register("confirmPassword")}
                                    />
                                    <button
                                        type="button"
                                        className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                                        onClick={() => setShowConfirm(!showConfirm)}
                                    >
                                        {showConfirm ? <EyeOff size={18} /> : <Eye size={18} />}
                                    </button>
                                </div>
                                {form.formState.errors.confirmPassword && (
                                    <Alert variant="destructive">
                                        <AlertDescription>
                                            {form.formState.errors.confirmPassword.message}
                                        </AlertDescription>
                                    </Alert>
                                )}
                            </div>

                            {/* SUBMIT */}
                            <StarBorder className='w-full'>
                                <Button
                                    type="submit"
                                    className="w-full gradient-brand text-white cursor-pointer"
                                    disabled={isPending}
                                >
                                    {isPending ? "Restableciendo..." : "Restablecer Contraseña"}
                                </Button>
                            </StarBorder>

                        </form>
                    </CardContent>
                </Card>
            )}

        </div>
    )
}
