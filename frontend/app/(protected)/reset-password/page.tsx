"use client"

import type React from "react"

import { useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import Image from "next/image"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { ArrowLeft } from "lucide-react"
import GradientText from '@/components/GradientText'

export default function ResetPasswordPage() {
    const router = useRouter()
    const searchParams = useSearchParams()
    const token = searchParams.get("token")

    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [error, setError] = useState("")
    const [isLoading, setIsLoading] = useState(false)

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError("")

        if (password !== confirmPassword) {
            setError("Las contraseñas no coinciden")
            return
        }

        if (password.length < 6) {
            setError("La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (!token) {
            setError("Token inválido o expirado")
            return
        }

        setIsLoading(true)

        try {
            // Mock API call - in production this would reset the password
            await new Promise((resolve) => setTimeout(resolve, 1500))

            // Redirect to login after successful reset
            router.push("/login?reset=true")
        } catch (err) {
            setError("Error al restablecer la contraseña. Intenta nuevamente.")
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">
            <Card className="w-full max-w-md">
                <CardHeader className="space-y-4">
                    <Button variant="ghost" asChild className="w-fit cursor-pointer">
                        <Link href="/login">
                            <ArrowLeft className="mr-2 h-4 w-4" />
                            Volver al login
                        </Link>
                    </Button>
                    <div className="flex justify-center">
                        <div className="relative h-16 w-16">
                            <Image src="/logo.png" alt="Ticketera" fill className="object-contain" />
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
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="password">Nueva Contraseña</Label>
                            <Input
                                id="password"
                                type="password"
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        <div className="space-y-2">
                            <Label htmlFor="confirmPassword">Confirmar Contraseña</Label>
                            <Input
                                id="confirmPassword"
                                type="password"
                                placeholder="••••••••"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>

                        {error && (
                            <Alert variant="destructive">
                                <AlertDescription>{error}</AlertDescription>
                            </Alert>
                        )}

                        <Button type="submit" className="w-full gradient-brand text-white cursor-pointer" disabled={isLoading}>
                            {isLoading ? "Restableciendo..." : "Restablecer Contraseña"}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
