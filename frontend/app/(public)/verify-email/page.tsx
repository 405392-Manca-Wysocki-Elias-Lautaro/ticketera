"use client"

import Image from "next/image"
import Link from "next/link"
import { useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Mail, ArrowRight, RotateCw } from "lucide-react"
import { useEffect } from "react"
import { useResendVerificationEmail } from "@/hooks/auth/useResendVerificationEmail"
import GradientText from '@/components/GradientText'

export default function VerifyEmailPage() {
    const searchParams = useSearchParams()
    const email = searchParams.get("email")

    const { resend, isLoading, success, error, cooldown, setCooldown } =
        useResendVerificationEmail(30)

    useEffect(() => {
        setCooldown(30)
    }, [setCooldown])

    const handleResendEmail = async () => {
        if (!email) return
        await resend(email)
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">
            <Card className="w-full max-w-md text-center">
                <CardHeader className="space-y-4">
                    <div className="flex justify-center">
                        <div className="relative h-16 w-16">
                            <Image src="/logo.png" alt="Ticketera" fill className="object-contain" />
                        </div>
                    </div>

                    <div className="flex justify-center">
                        <div className="rounded-full bg-primary/10 p-4">
                            <Mail className="h-12 w-12 text-primary" />
                        </div>
                    </div>

                    <div>
                        <GradientText>
                            <CardTitle className="text-2xl">Verifica tu Email</CardTitle>
                        </GradientText>
                        <CardDescription className="text-base mt-2">
                            Te hemos enviado un correo de verificación
                        </CardDescription>
                    </div>
                </CardHeader>

                <CardContent className="space-y-4">
                    <p className="text-muted-foreground">
                        Revisa tu bandeja de entrada y haz clic en el enlace de verificación para activar tu cuenta.
                    </p>

                    <div className="bg-muted/50 p-4 rounded-lg text-sm text-muted-foreground">
                        <p className="font-medium mb-1">¿No recibiste el correo?</p>
                        <p>Revisa tu carpeta de spam o solicita un nuevo correo de verificación.</p>
                    </div>

                    <div className="space-y-2">
                        {error && (
                            <div className="bg-destructive/10 border border-destructive/30 text-destructive rounded-lg p-3 text-sm">
                                {error}
                            </div>
                        )}
                        {success && (
                            <div className="bg-green-500/10 border border-green-500/30 text-green-700 rounded-lg p-3 text-sm">
                                Correo reenviado exitosamente. Revisa tu bandeja de entrada.
                            </div>
                        )}

                        <Button
                            onClick={handleResendEmail}
                            disabled={isLoading || cooldown > 0}
                            variant="outline"
                            className="w-full cursor-pointer bg-transparent"
                        >
                            {isLoading ? (
                                <>
                                    <RotateCw className="mr-2 h-4 w-4 animate-spin" />
                                    Reenviando...
                                </>
                            ) : cooldown > 0 ? (
                                <>
                                    <RotateCw className="mr-2 h-4 w-4" />
                                    Reenviar en {cooldown}s
                                </>
                            ) : (
                                <>
                                    <RotateCw className="mr-2 h-4 w-4" />
                                    Reenviar correo de verificación
                                </>
                            )}
                        </Button>
                    </div>

                    <Button asChild className="w-full gradient-brand text-white cursor-pointer">
                        <Link href="/login">
                            Ir al Login
                            <ArrowRight className="ml-2 h-4 w-4" />
                        </Link>
                    </Button>
                </CardContent>
            </Card>
        </div>
    )
}
