"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { User, Mail, Phone, MapPin, Save, Loader2, ArrowLeft } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { Navbar } from '@/components/Navbar'
import GradientText from '@/components/GradientText'
import { RoleUtils } from '@/utils/roleUtils'
import StarBorder from '@/components/StarBorder'
import Link from 'next/link'
import { StaffSidebar } from '@/components/sidebars/StaffSidebar'

export default function ProfilePage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()
    const [isSaving, setIsSaving] = useState(false)

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsSaving(true)
        await new Promise((resolve) => setTimeout(resolve, 1000))
        setIsSaving(false)
    }

    if (isLoading || !user) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="flex min-h-screen bg-background">

            {!RoleUtils.isStaff(user) && (
                <Navbar />
            )}

            {RoleUtils.isStaff(user) && (
                <div className="fixed left-0 h-full">
                    <StaffSidebar />
                </div>
            )}

            <div className="container mx-auto px-4 py-8 max-w-2xl">

                <div className="relative flex items-center justify-center mb-8">
                    {RoleUtils.isCustomer(user) && (
                        <div className="absolute left-0">
                            <Button variant="ghost" asChild className="cursor-pointer">
                                <Link href="/dashboard">
                                    <ArrowLeft className="mr-2 h-4 w-4" />
                                    Volver al inicio
                                </Link>
                            </Button>
                        </div>
                    )}

                    <GradientText>
                        <h1 className="text-3xl font-bold text-center">Mi Perfil</h1>
                    </GradientText>
                </div>

                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Información Personal</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <form onSubmit={handleSave} className="space-y-4">
                                <div className="space-y-2">
                                    <Label htmlFor="name">Nombre Completo</Label>
                                    <div className="relative">
                                        <User className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                        <Input id="name" defaultValue={user.firstName} className="pl-9" />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="email">Email</Label>
                                    <div className="relative">
                                        <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                        <Input id="email" type="email" defaultValue={user.email} className="pl-9" />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="phone">Teléfono</Label>
                                    <div className="relative">
                                        <Phone className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                        <Input id="phone" type="tel" placeholder="+54 11 1234-5678" className="pl-9" />
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <Label htmlFor="address">Dirección</Label>
                                    <div className="relative">
                                        <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                        <Input id="address" placeholder="Calle, Ciudad, País" className="pl-9" />
                                    </div>
                                </div>

                                <StarBorder className='w-full'>
                                    <Button type="submit" className="w-full gradient-brand text-white" disabled={isSaving}>
                                        {isSaving ? (
                                            <>
                                                <Loader2 className="animate-spin" />
                                            </>
                                        ) : (
                                            <>
                                                <Save className="mr-2 h-4 w-4" />
                                                Guardar Cambios
                                            </>
                                        )}
                                    </Button>
                                </StarBorder>
                            </form>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Información de Cuenta</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {!RoleUtils.isCustomer(user) && (
                                <div className="flex justify-between items-center">
                                    <div>
                                        <p className="font-medium">Rol de Usuario</p>
                                        <p className="text-sm text-muted-foreground capitalize">{user.role.description}</p>
                                    </div>
                                </div>
                            )}

                            <div className="flex justify-between items-center">
                                <div>
                                    <p className="font-medium">Contraseña</p>
                                    <p className="text-sm text-muted-foreground">••••••••</p>
                                </div>
                                <Button variant="outline" size="sm" className="cursor-pointer bg-transparent">
                                    Cambiar
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    )
}
