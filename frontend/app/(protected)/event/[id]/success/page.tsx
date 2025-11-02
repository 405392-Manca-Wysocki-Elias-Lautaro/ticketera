"use client"

import { useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { CheckCircle2 } from "lucide-react"
import Link from "next/link"
import Confetti from "react-confetti"
import { useAuth } from '@/hooks/auth/useAuth'
import { useWindowSize } from '@/hooks/useWindowSize'
import { Navbar } from '@/components/Navbar'

export default function SuccessPage() {
  const router = useRouter()
  const params = useParams()
  const { user, isLoading } = useAuth()
  const { width, height } = useWindowSize()

  useEffect(() => {
    if (!isLoading && !user) {
      router.push("/login")
    }
  }, [user, isLoading, router])

  if (isLoading || !user) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background">
      <Confetti width={width} height={height} recycle={false} numberOfPieces={500} />
      <Navbar />

      <main className="container mx-auto px-4 py-16">
        <Card className="max-w-2xl mx-auto text-center">
          <CardContent className="pt-12 pb-8 space-y-6">
            <div className="flex justify-center">
              <CheckCircle2 className="h-20 w-20 text-green-500" />
            </div>

            <div>
              <h1 className="text-3xl font-bold mb-2">¡Compra Exitosa!</h1>
              <p className="text-muted-foreground text-lg">Tu pago ha sido procesado correctamente</p>
            </div>

            <div className="bg-muted/50 rounded-lg p-6 space-y-2">
              <p className="text-sm text-muted-foreground">Hemos enviado tus entradas a</p>
              <p className="font-semibold text-lg">{user.email}</p>
            </div>

            <p className="text-sm text-muted-foreground">
              Puedes ver y descargar tus entradas en cualquier momento desde tu perfil
            </p>

            <div className="flex flex-col sm:flex-row gap-3 justify-center pt-4">
              <Button asChild size="lg" className="gradient-brand text-white">
                <Link href="/my-tickets">Ver Mis Tickets</Link>
              </Button>
              <Button asChild variant="outline" size="lg">
                <Link href="/dashboard">Volver al Inicio</Link>
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
