"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Card, CardContent } from "@/components/ui/card"
import { CheckCircle2, Loader2 } from "lucide-react"
import { Navbar } from '@/components/Navbar'
import api from "@/lib/api"

export default function PaymentSuccessPage() {
    const router = useRouter()
    const searchParams = useSearchParams()
    const [eventId, setEventId] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [countdown, setCountdown] = useState(5)

    useEffect(() => {
        const orderId = searchParams.get("orderId")
        const preferenceId = searchParams.get("preference_id")
        const externalReference = searchParams.get("external_reference") || orderId

        if (!externalReference) {
            console.error("No orderId or external_reference found in URL")
            setIsLoading(false)
            return
        }

        // Obtener el eventId desde el backend
        const fetchEventId = async () => {
            try {
                const response = await api.get(`/api/payments/orders/${externalReference}/event-id`)
                // El gateway puede devolver la respuesta envuelta en data.data o directamente en data
                const eventIdData = response.data?.data || response.data
                if (eventIdData && eventIdData.eventId) {
                    setEventId(eventIdData.eventId)
                    setIsLoading(false)
                } else {
                    console.error("Event ID not found in response", response.data)
                    setIsLoading(false)
                }
            } catch (error) {
                console.error("Error fetching event ID:", error)
                setIsLoading(false)
            }
        }

        fetchEventId()
    }, [searchParams])

    // Redirección automática después de 5 segundos
    useEffect(() => {
        if (eventId && !isLoading) {
            const interval = setInterval(() => {
                setCountdown((prev) => {
                    if (prev <= 1) {
                        clearInterval(interval)
                        router.push("/my-tickets")
                        return 0
                    }
                    return prev - 1
                })
            }, 1000)

            return () => clearInterval(interval)
        }
    }, [eventId, isLoading, router])

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-16">
                <Card className="max-w-2xl mx-auto text-center">
                    <CardContent className="pt-12 pb-8 space-y-6">
                        {isLoading ? (
                            <>
                                <div className="flex justify-center">
                                    <Loader2 className="h-20 w-20 text-primary animate-spin" />
                                </div>
                                <div>
                                    <h1 className="text-3xl font-bold mb-2">Procesando pago...</h1>
                                    <p className="text-muted-foreground text-lg">
                                        Verificando tu compra
                                    </p>
                                </div>
                            </>
                        ) : eventId ? (
                            <>
                                <div className="flex justify-center">
                                    <CheckCircle2 className="h-20 w-20 text-green-500" />
                                </div>

                                <div>
                                    <h1 className="text-3xl font-bold mb-2">¡Pago Exitoso!</h1>
                                    <p className="text-muted-foreground text-lg">
                                        Tu pago ha sido procesado correctamente
                                    </p>
                                </div>

                                <div className="bg-muted/50 rounded-lg p-6 space-y-2">
                                    <p className="text-sm text-muted-foreground">
                                        Redirigiendo a la página de éxito en...
                                    </p>
                                    <p className="font-semibold text-2xl text-primary">
                                        {countdown} segundos
                                    </p>
                                </div>

                                <p className="text-sm text-muted-foreground">
                                    Si no eres redirigido automáticamente,{" "}
                                    <button
                                        onClick={() => router.push("/my-tickets")}
                                        className="text-primary hover:underline font-semibold"
                                    >
                                        haz clic aquí
                                    </button>
                                </p>
                            </>
                        ) : (
                            <>
                                <div className="flex justify-center">
                                    <CheckCircle2 className="h-20 w-20 text-green-500" />
                                </div>

                                <div>
                                    <h1 className="text-3xl font-bold mb-2">¡Pago Exitoso!</h1>
                                    <p className="text-muted-foreground text-lg">
                                        Tu pago ha sido procesado correctamente
                                    </p>
                                </div>

                                <p className="text-sm text-muted-foreground">
                                    No se pudo obtener la información del evento.{" "}
                                    <button
                                        onClick={() => router.push("/my-tickets")}
                                        className="text-primary hover:underline font-semibold"
                                    >
                                        Ver mis tickets
                                    </button>
                                </p>
                            </>
                        )}
                    </CardContent>
                </Card>
            </main>
        </div>
    )
}

