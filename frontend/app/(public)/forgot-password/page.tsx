"use client"

import type React from "react"

import { useState } from "react"
import Image from "next/image"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ArrowLeft, CheckCircle } from "lucide-react"

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [emailSent, setEmailSent] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      // Mock API call - in production this would send a reset email
      await new Promise((resolve) => setTimeout(resolve, 1500))
      setEmailSent(true)
    } catch (err) {
      // Handle error
    } finally {
      setIsLoading(false)
    }
  }

  if (emailSent) {
    return (
      <div className="min-h-screen flex items-center justify-center p-4 gradient-brand">
        <Card className="w-full max-w-md">
          <CardHeader className="space-y-4 text-center">
            <div className="flex justify-center">
              <CheckCircle className="h-16 w-16 text-green-500" />
            </div>
            <div>
              <CardTitle className="text-2xl gradient-text">Email Enviado</CardTitle>
              <CardDescription>
                Hemos enviado un enlace de recuperación a <strong>{email}</strong>
              </CardDescription>
            </div>
          </CardHeader>
          <CardContent className="space-y-4">
            <p className="text-sm text-muted-foreground text-center">
              Revisa tu bandeja de entrada y sigue las instrucciones para restablecer tu contraseña.
            </p>
            <Button asChild className="w-full cursor-pointer">
              <Link href="/login">Volver al login</Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    )
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
            <CardTitle className="text-2xl gradient-text">Recuperar Contraseña</CardTitle>
            <CardDescription>Ingresa tu email para recibir un enlace de recuperación</CardDescription>
          </div>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="tu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <Button type="submit" className="w-full gradient-brand text-white cursor-pointer" disabled={isLoading}>
              {isLoading ? "Enviando..." : "Enviar Enlace de Recuperación"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
