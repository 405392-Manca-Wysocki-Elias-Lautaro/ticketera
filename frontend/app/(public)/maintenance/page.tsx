"use client"

import Image from "next/image"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Wrench } from "lucide-react"

export default function MaintenancePage() {
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
            <Wrench className="h-16 w-16 text-primary" />
          </div>
          <div>
            <CardTitle className="text-3xl gradient-text">Sitio en Mantenimiento</CardTitle>
            <CardDescription className="text-lg">Estamos trabajando para mejorar tu experiencia</CardDescription>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-muted-foreground">
            Nuestro equipo está realizando mejoras en la plataforma. Volveremos pronto con nuevas funcionalidades.
          </p>
          <p className="text-sm text-muted-foreground">Gracias por tu paciencia.</p>
        </CardContent>
      </Card>
    </div>
  )
}
