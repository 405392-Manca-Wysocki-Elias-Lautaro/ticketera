"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

import Image from "next/image"
import Link from "next/link"
import { ArrowLeft, Eye, EyeOff, Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import StarBorder from "@/components/StarBorder"
import { signUpSchema, SignUpSchema } from "@/schemas/auth/SignUpSchema"
import { useRegister } from '@/hooks/auth/useRegister'
import { useState } from 'react'
import PasswordStrengthIndicator from '@/components/auth/PasswordStrengthIndicator'
import GradientText from '@/components/GradientText'
import { Checkbox } from "@/components/ui/checkbox"
import { TermsModal } from "@/components/auth/TermsModal"

export default function SignUpPage() {

    const { mutateAsync: registerUser, isPending, isError, error, isSuccess } = useRegister()
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);

    const {
        register,
        handleSubmit,
        watch,
        setValue,
        formState: { errors, isValid },
    } = useForm<SignUpSchema>({
        resolver: zodResolver(signUpSchema),
        defaultValues: {
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            confirmPassword: "",
            termsAccepted: false,
        },
        mode: "onChange",
    })

    const onSubmit = async (data: SignUpSchema) => {
        try {
            await registerUser(data)
        } catch (err) {
            console.error("❌ Error al registrarse:", err)
        }
    }

    return (
        <div className="h-screen w-full grid place-items-center p-4 gradient-brand overflow-y-auto">
            <Card className="w-full max-w-md gap-2">
                <CardHeader className="space-y-4">
                    <Button variant="ghost" asChild className="w-fit cursor-pointer mb-0">
                        <Link href="/login">
                            <ArrowLeft className="mr-2 h-4 w-4" />
                            Volver al login
                        </Link>
                    </Button>

                    <div className="flex justify-center mb-0">
                        <div className="relative h-16 w-16">
                            <Image src="/logo.png" alt="Ticketly" fill className="object-contain" />
                        </div>
                    </div>

                    <div className="text-center">
                        <GradientText>
                            <CardTitle className="text-2xl">Crear Cuenta</CardTitle>
                        </GradientText>
                        <CardDescription>Completa tus datos para registrarte</CardDescription>
                    </div>
                </CardHeader>

                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                        {/* Nombre */}
                        <div className="space-y-2">
                            <Label htmlFor="firstName">Nombre</Label>
                            <Input id="firstName" type="text" placeholder="Nombre" {...register("firstName")} />
                            {errors.firstName && <p className="text-sm text-destructive">{errors.firstName.message}</p>}
                        </div>

                        {/* Apellido */}
                        <div className="space-y-2">
                            <Label htmlFor="lastName">Apellido</Label>
                            <Input id="lastName" type="text" placeholder="Apellido" {...register("lastName")} />
                            {errors.lastName && <p className="text-sm text-destructive">{errors.lastName.message}</p>}
                        </div>

                        {/* Email */}
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input id="email" type="email" placeholder="user@email.com" {...register("email")} />
                            {errors.email && <p className="text-sm text-destructive">{errors.email.message}</p>}
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Contraseña</Label>
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
                            <PasswordStrengthIndicator password={watch("password")} />
                        </div>

                        {/* Confirm Password */}
                        <div className="space-y-2">
                            <Label htmlFor="confirmPassword">Confirmar Contraseña</Label>
                            <div className="relative">
                                <Input
                                    id="confirmPassword"
                                    type={showConfirm ? "text" : "password"}
                                    placeholder="••••••••"
                                    {...register("confirmPassword")}
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-2.5 text-gray-500 hover:text-gray-700"
                                    onClick={() => setShowConfirm(!showConfirm)}
                                >
                                    {showConfirm ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                            {errors.confirmPassword && (
                                <p className="text-sm text-destructive">{errors.confirmPassword.message}</p>
                            )}
                        </div>

                        {/* Términos y Condiciones */}
                        <div className="flex items-start space-x-2">
                            <Checkbox
                                id="terms"
                                checked={watch("termsAccepted")}
                                onCheckedChange={(checked) => setValue("termsAccepted", checked as boolean, { shouldValidate: true })}
                            />
                            <div className="grid gap-1.5 leading-none">
                                <label
                                    htmlFor="terms"
                                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                                >
                                    Acepto los <TermsModal />
                                </label>
                                {errors.termsAccepted && (
                                    <p className="text-sm text-destructive">{errors.termsAccepted.message}</p>
                                )}
                            </div>
                        </div>

                        {/* Errores generales */}
                        {(Object.keys(errors).length > 0 || isError) && (
                            <Alert variant="destructive">
                                <AlertDescription>
                                    {isError
                                        ? (error as any)?.response?.data?.message ??
                                        "Error al crear la cuenta. Intenta nuevamente."
                                        : "Por favor corrige los errores antes de continuar."}
                                </AlertDescription>
                            </Alert>
                        )}

                        {/* Botón */}
                        <StarBorder className='w-full'>
                            <Button
                                type="submit"
                                className="w-full gradient-brand text-white cursor-pointer"
                                disabled={isPending || isSuccess || !isValid}
                            >
                                {(isPending || isSuccess) ? <Loader2 className="animate-spin" /> : "Crear Cuenta"}
                            </Button>
                        </StarBorder>
                    </form>

                    <div className="mt-4 text-center text-sm text-muted-foreground">
                        ¿Ya tienes cuenta?{" "}
                        <Link href="/login" className="text-primary hover:underline cursor-pointer">
                            Inicia sesión
                        </Link>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
