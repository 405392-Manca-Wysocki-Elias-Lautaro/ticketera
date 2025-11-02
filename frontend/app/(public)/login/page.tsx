"use client"

import Image from "next/image"
import Link from "next/link"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { useLogin } from "@/hooks/auth/useLogin"
import StarBorder from "@/components/StarBorder"
import { Loader2 } from "lucide-react"
import { Checkbox } from '@/components/ui/checkbox'
import { loginSchema, type LoginSchema } from '@/schemas/auth/LoginSchema'
import { Label } from '@/components/ui/label'

export default function LoginPage() {
    const { mutate: login, isPending } = useLogin()

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginSchema>({
        resolver: zodResolver(loginSchema),
        mode: "onBlur",
        defaultValues: {
            email: "",
            password: "",
            remembered: false,
        },
    })

    const onSubmit = (data: LoginSchema) => {
        login(data)
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">
            <Card className="w-full max-w-md">
                <CardHeader className="space-y-4 text-center">
                    <div className="flex justify-center">
                        <div className="relative h-16 w-16">
                            <Image src="/logo.png" alt="Ticketera" fill className="object-contain" />
                        </div>
                    </div>
                    <div>
                        <CardTitle className="text-2xl gradient-text">Bienvenido a Ticketera</CardTitle>
                        <CardDescription>Ingresa tus credenciales para continuar</CardDescription>
                    </div>
                </CardHeader>

                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                        {/* Email */}
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                placeholder="tu@email.com"
                                {...register("email")}
                            />
                            {errors.email && (
                                <p className="text-sm text-destructive">{errors.email.message}</p>
                            )}
                        </div>

                        {/* Password */}
                        <div className="space-y-2">
                            <div className="flex items-center justify-between">
                                <Label htmlFor="password">Contraseña</Label>
                                <Link
                                    href="/forgot-password"
                                    className="text-xs text-primary hover:underline cursor-pointer"
                                >
                                    ¿Olvidaste tu contraseña?
                                </Link>
                            </div>
                            <Input
                                id="password"
                                type="password"
                                placeholder="••••••••"
                                {...register("password")}
                            />
                            {errors.password && (
                                <p className="text-sm text-destructive">{errors.password.message}</p>
                            )}
                        </div>

                        {/* Remember me (opcional futuro) */}
                        <div className="flex items-center gap-2">
                            <Checkbox id="remembered" {...register("remembered")} />
                            <Label htmlFor="remembered" className="text-sm">
                                Recordarme
                            </Label>
                        </div>

                        <StarBorder>
                            <Button
                                type="submit"
                                className="w-full gradient-brand text-white"
                                disabled={isPending}
                            >
                                {isPending ? (
                                    <Loader2 className="animate-spin" />
                                ) : (
                                    "Iniciar Sesión"
                                )}
                            </Button>
                        </StarBorder>
                    </form>

                    <div className="mt-4 text-center text-sm text-muted-foreground">
                        ¿No tienes cuenta?{" "}
                        <Link href="/signup" className="text-primary hover:underline cursor-pointer">
                            Regístrate
                        </Link>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
