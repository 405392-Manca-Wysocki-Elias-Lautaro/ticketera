"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { AlertCircle, Home } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { RoleCode } from '@/types/enums/RoleCode'

export default function NotFound() {
    const router = useRouter()
    const { user, isLoading } = useAuth()
    const [redirectPath, setRedirectPath] = useState("/dashboard")

    useEffect(() => {
        if (!isLoading && user) {
            // Determine redirect path based on role
            if (user.role.code === RoleCode.ADMIN) {
                setRedirectPath("/admin")
            } else if (user.role.code === RoleCode.STAFF) {
                setRedirectPath("/staff")
            } else {
                setRedirectPath("/dashboard")
            }
        }
    }, [user, isLoading])

    const handleGoHome = () => {
        router.push(redirectPath)
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">
            <Card className="w-full max-w-md text-center">
                <CardHeader className="space-y-4">
                    <div className="flex justify-center">
                        <AlertCircle className="h-16 w-16 text-destructive" />
                    </div>
                    <div>
                        <CardTitle className="text-3xl gradient-text">404</CardTitle>
                        <CardDescription className="text-lg">Página no encontrada</CardDescription>
                    </div>
                </CardHeader>
                <CardContent className="space-y-4">
                    <p className="text-muted-foreground">
                        La página que buscas no existe o no tienes permisos para acceder a ella.
                    </p>
                    <Button onClick={handleGoHome} className="w-full gradient-brand text-white cursor-pointer">
                        <Home className="mr-2 h-4 w-4" />
                        Ir al Inicio
                    </Button>
                </CardContent>
            </Card>
        </div>
    )
}
