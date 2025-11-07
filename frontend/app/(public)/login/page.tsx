"use client"

import Image from "next/image"
import Link from "next/link"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { useLogin } from "@/hooks/auth/useLogin"
import StarBorder from "@/components/StarBorder"
import { Eye, EyeOff, Loader2 } from "lucide-react"
import { Checkbox } from '@/components/ui/checkbox'
import { loginSchema, type LoginSchema } from '@/schemas/auth/LoginSchema'
import { Label } from '@/components/ui/label'
import { useState } from 'react'
import GradientText from '@/components/GradientText'

export default function LoginPage() {

    const [showPassword, setShowPassword] = useState(false);

    const { mutate: login, isPending } = useLogin()

    const {
        register,
        handleSubmit,
        control,
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
                        <GradientText>
                            <CardTitle className="text-2xl">Bienvenido a Ticketera</CardTitle>
                        </GradientText>
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
                            <div className="relative">
                                <Input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    placeholder="••••••••"
                                    {...register("password")}
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                            {errors.password && (
                                <p className="text-sm text-destructive">{errors.password.message}</p>
                            )}
                        </div>

                        {/* Remember me */}
                        <Controller
                            name="remembered"
                            control={control}
                            render={({ field }) => (
                                <div className="flex items-center gap-2">
                                    <Checkbox
                                        id="remembered"
                                        checked={field.value}
                                        onCheckedChange={(checked: any) => field.onChange(checked === true)}
                                    />
                                    <Label htmlFor="remembered" className="text-sm">
                                        Recordarme
                                    </Label>
                                </div>
                            )}
                        />

                        <StarBorder className='w-full'>
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
