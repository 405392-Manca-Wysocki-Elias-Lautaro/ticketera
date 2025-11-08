"use client"

import Image from "next/image"
import Link from "next/link"
import { useEffect, useState } from "react"
import { useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { CheckCircle2, Loader2, AlertCircle, ArrowRight } from "lucide-react"
import { useVerifyEmail } from "@/hooks/auth/useVerifyEmail"
import GradientText from '@/components/GradientText'

export default function EmailVerifiedPage() {
    const searchParams = useSearchParams()
    const token = searchParams.get("token")

    const { verify, isLoading, success, error } = useVerifyEmail()
    const [hasVerified, setHasVerified] = useState(false)
    const [isVerifying, setIsVerifying] = useState(true) // ✅ nuevo estado

    useEffect(() => {
        if (token && !hasVerified) {
            verify(token)
            setHasVerified(true)
        }

        // Mantenemos el loader visible un frame antes de cambiar
        const timeout = setTimeout(() => setIsVerifying(false), 200)
        return () => clearTimeout(timeout)
    }, [token, hasVerified, verify])

    const showLoader = isLoading || isVerifying

    return (
        <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">
            <Card className="w-full max-w-md text-center">
                <CardHeader className="space-y-4">
                    <div className="flex justify-center">
                        <div className="relative h-16 w-16">
                            <Image src="/logo.png" alt="Ticketera" fill className="object-contain" />
                        </div>
                    </div>

                    {showLoader && (
                        <>
                            <div className="flex justify-center">
                                <div className="rounded-full bg-primary/10 p-4">
                                    <Loader2 className="h-12 w-12 text-primary animate-spin" />
                                </div>
                            </div>
                            <div>
                                <GradientText>
                                    <CardTitle className="text-2xl">Verificando tu email...</CardTitle>
                                </GradientText>
                                <CardDescription className="text-base mt-2">
                                    Por favor espera mientras verificamos tu cuenta
                                </CardDescription>
                            </div>
                        </>
                    )}

                    {!showLoader && success && (
                        <>
                            <div className="flex justify-center">
                                <div className="rounded-full bg-green-500/10 p-4">
                                    <CheckCircle2 className="h-12 w-12 text-green-500" />
                                </div>
                            </div>
                            <div>
                                <GradientText>
                                    <CardTitle className="text-2xl">¡Email verificado!</CardTitle>
                                </GradientText>
                                <CardDescription className="text-base mt-2">
                                    Tu cuenta ha sido activada exitosamente
                                </CardDescription>
                            </div>
                        </>
                    )}

                    {!showLoader && error && (
                        <>
                            <div className="flex justify-center">
                                <div className="rounded-full bg-destructive/10 p-4">
                                    <AlertCircle className="h-12 w-12 text-destructive" />
                                </div>
                            </div>
                            <div>
                                <GradientText>
                                    <CardTitle className="text-2xl ">Error en la verificación</CardTitle>
                                </GradientText>
                                <CardDescription className="text-base mt-2">{error}</CardDescription>
                            </div>
                        </>
                    )}
                </CardHeader>

                {!showLoader && (
                    <CardContent className="space-y-4">
                        {success ? (
                            <>
                                <p className="text-muted-foreground">
                                    Tu cuenta está lista para usar. Inicia sesión para acceder a la plataforma.
                                </p>
                                <Button asChild className="w-full gradient-brand text-white cursor-pointer">
                                    <Link href="/login">
                                        Ir al Login
                                        <ArrowRight className="ml-2 h-4 w-4" />
                                    </Link>
                                </Button>
                            </>
                        ) : (
                            <>
                                <p className="text-muted-foreground">
                                    El enlace de verificación puede haber expirado o ser inválido.
                                </p>
                                <Button asChild className="w-full gradient-brand text-white cursor-pointer">
                                    <Link href="/verify-email">
                                        Solicitar nuevo correo
                                        <ArrowRight className="ml-2 h-4 w-4" />
                                    </Link>
                                </Button>
                                <Button asChild variant="outline" className="w-full cursor-pointer bg-transparent">
                                    <Link href="/login">Ir al Login</Link>
                                </Button>
                            </>
                        )}
                    </CardContent>
                )}
            </Card>
        </div>
    )
}
