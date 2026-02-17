"use client"

import { useEffect, useState } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { CheckCircle2, Loader2, X } from "lucide-react"
import { Navbar } from '@/components/Navbar'
import api from "@/lib/api"

export default function PaymentSuccessPage() {
    const router = useRouter()
    const searchParams = useSearchParams()
    const [isLoading, setIsLoading] = useState(true)
    const [paymentStatus, setPaymentStatus] = useState<"success" | "pending" | "error">("pending")
    const [countdown, setCountdown] = useState(5)
    const [canClose, setCanClose] = useState(false)

    useEffect(() => {
        const orderId = searchParams.get("orderId")
        const externalReference = searchParams.get("external_reference") || orderId

        if (!externalReference) {
            console.error("No orderId or external_reference found in URL")
            setIsLoading(false)
            setPaymentStatus("success")
            return
        }

        const fetchStatus = async () => {
            try {
                const response = await api.get(`/api/payments/orders/${externalReference}/event-id`)
                const data = response.data?.data || response.data
                if (data && data.eventId) {
                    setPaymentStatus("success")
                } else {
                    setPaymentStatus("success")
                }
            } catch (error) {
                console.error("Error fetching order info:", error)
                setPaymentStatus("success")
            } finally {
                setIsLoading(false)
            }
        }

        fetchStatus()
    }, [searchParams])

    // Detectar si esta pestaña fue abierta por JavaScript (window.open)
    useEffect(() => {
        setCanClose(!!window.opener)
    }, [])

    // Intentar cerrar la pestaña automáticamente después de 5 segundos si fue abierta por JS
    useEffect(() => {
        if (isLoading || paymentStatus !== "success") return

        if (canClose) {
            const interval = setInterval(() => {
                setCountdown((prev) => {
                    if (prev <= 1) {
                        clearInterval(interval)
                        window.close()
                        return 0
                    }
                    return prev - 1
                })
            }, 1000)

            return () => clearInterval(interval)
        } else {
            // Si no fue abierta por JS, redirigir a /my-tickets como antes
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
    }, [isLoading, paymentStatus, canClose, router])

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
                        ) : (
                            <>
                                <div className="flex justify-center">
                                    <CheckCircle2 className="h-20 w-20 text-green-500" />
                                </div>

                                <div>
                                    <h1 className="text-3xl font-bold mb-2">Pago Exitoso!</h1>
                                    <p className="text-muted-foreground text-lg">
                                        Tu pago ha sido procesado correctamente
                                    </p>
                                </div>

                                {canClose ? (
                                    <>
                                        <div className="bg-muted/50 rounded-lg p-6 space-y-2">
                                            <p className="text-sm text-muted-foreground">
                                                Esta pestana se cerrara automaticamente en...
                                            </p>
                                            <p className="font-semibold text-2xl text-primary">
                                                {countdown} segundos
                                            </p>
                                            <p className="text-xs text-muted-foreground">
                                                La pagina principal se actualizara sola con la confirmacion.
                                            </p>
                                        </div>

                                        <div className="flex flex-col gap-2">
                                            <Button
                                                onClick={() => window.close()}
                                                className="gap-2"
                                            >
                                                <X className="h-4 w-4" />
                                                Cerrar esta pestana
                                            </Button>
                                            <button
                                                onClick={() => router.push("/my-tickets")}
                                                className="text-sm text-primary hover:underline font-semibold"
                                            >
                                                O ir a Mis Tickets
                                            </button>
                                        </div>
                                    </>
                                ) : (
                                    <>
                                        <div className="bg-muted/50 rounded-lg p-6 space-y-2">
                                            <p className="text-sm text-muted-foreground">
                                                Redirigiendo a tus tickets en...
                                            </p>
                                            <p className="font-semibold text-2xl text-primary">
                                                {countdown} segundos
                                            </p>
                                        </div>

                                        <p className="text-sm text-muted-foreground">
                                            Si no sos redirigido automaticamente,{" "}
                                            <button
                                                onClick={() => router.push("/my-tickets")}
                                                className="text-primary hover:underline font-semibold"
                                            >
                                                hace clic aqui
                                            </button>
                                        </p>
                                    </>
                                )}
                            </>
                        )}
                    </CardContent>
                </Card>
            </main>
        </div>
    )
}
