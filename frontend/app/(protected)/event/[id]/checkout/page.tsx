"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter, useParams, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { ArrowLeft, Loader2, ChevronDown, CreditCard } from "lucide-react"
import Link from "next/link"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockEvents } from '@/mocks/mockEvents'
import { Navbar } from '@/components/Navbar'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'
import { useMercadoPagoCheckout } from '@/hooks/useMercadoPagoCheckout'
import { orderService } from '@/services/orderService'
import type { CreateOrderRequest } from '@/types/Order'
import { toast } from 'sonner'

export default function CheckoutPage() {
    const router = useRouter()
    const params = useParams()
    const searchParams = useSearchParams()
    const { user, isLoading } = useAuth()
    const [event, setEvent] = useState(mockEvents.find((e) => e.id === params.id))
    const [isProcessing, setIsProcessing] = useState(false)
    const [preferenceId, setPreferenceId] = useState<string | undefined>()
    const [phone, setPhone] = useState("")
    const [errors, setErrors] = useState<Record<string, string>>({})

    const areaId = searchParams.get("area")
    const seats = searchParams.get("seats")
    const quantity = searchParams.get("quantity")
    const total = Number(searchParams.get("total"))

    const selectedArea = event?.areas.find((a) => a.id === areaId)

    const parsedSeats = seats
        ? seats.split(",").map((s: string) => {
            const [row, seat] = s.split("-")
            return { row, seat: Number(seat) }
        })
        : []

    const serviceFee = Math.round(total * 0.1)
    const finalTotal = total + serviceFee

    // Hook de Mercado Pago
    const { isSDKReady, isLoading: isMPLoading, renderPaymentButton } = useMercadoPagoCheckout({
        preferenceId,
        onReady: () => {
            console.log("Botón de Mercado Pago renderizado exitosamente");
        },
        onError: (error) => {
            console.error("Error en Mercado Pago:", error);
            toast.error("Error al cargar el botón de pago");
        },
    });

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    // Renderizar el botón cuando el SDK esté listo y tengamos el preferenceId
    useEffect(() => {
        if (isSDKReady && preferenceId) {
            renderPaymentButton("wallet-container");
        }
    }, [isSDKReady, preferenceId, renderPaymentButton]);

    const validatePhone = (value: string) => {
        const cleaned = value.replace(/[\s\-()]/g, "")
        if (cleaned.length < 10 || !/^\+?\d+$/.test(cleaned)) {
            return "Teléfono inválido"
        }
        return ""
    }

    const handleCreateOrder = async (e: React.FormEvent) => {
        e.preventDefault()

        const newErrors: Record<string, string> = {}

        const phoneError = validatePhone(phone)
        if (phoneError) newErrors.phone = phoneError

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors)
            return
        }

        setIsProcessing(true)

        try {
            if (!user || !event || !selectedArea) {
                throw new Error("Información incompleta");
            }

            // Construir los items de la orden con datos reales
            // Convertir IDs de string a number para el backend
            const eventIdNum = parseInt(event.id) || 1;
            const areaIdNum = parseInt(areaId || "1");
            
            const items = parsedSeats.length > 0
                ? parsedSeats.map((seat: { row: string; seat: number }) => {
                    // Generar un ID único para el asiento basado en fila y número
                    // Ej: Fila "A" = 1, asiento 5 = 105
                    const rowCode = seat.row.charCodeAt(0) - 64; // A=1, B=2, etc
                    const seatId = rowCode * 100 + seat.seat;
                    
                    return {
                        eventId: eventIdNum,
                        venueAreaId: areaIdNum,
                        venueSeatId: seatId,
                        ticketTypeId: 1, // 1 = adulto estándar
                        unitPriceCents: selectedArea.price * 100,
                        quantity: 1,
                    };
                })
                : [{
                    // Área general (sin asiento específico)
                    eventId: eventIdNum,
                    venueAreaId: areaIdNum,
                    venueSeatId: undefined,
                    ticketTypeId: 1, // 1 = adulto estándar
                    unitPriceCents: selectedArea.price * 100,
                    quantity: parseInt(quantity || "1"),
                }];

            // Crear el request de la orden
            const orderRequest: CreateOrderRequest = {
                customer: {
                    email: user.email,
                    firstName: user.firstName || "Usuario",
                    lastName: user.lastName || "Apellido",
                    phone: phone,
                    userId: user.id, // UUID del usuario desde auth-service
                },
                organizerId: parseInt(event.id) || 1, // Usar el mismo ID del evento como organizerId temporal
                items: items,
                currency: "ARS",
                paymentDescription: `Entradas para ${event.title}`,
                notes: `Compra de ${items.length} entrada(s) para ${event.title} - ${selectedArea.name}`,
            };

            console.log("Creando orden:", orderRequest);

            // Crear la orden y obtener la URL de pago
            const orderResponse = await orderService.createOrder(orderRequest);

            console.log("Orden creada:", orderResponse);

            if (orderResponse.paymentUrl) {
                // Si el backend devuelve una URL directa, redirigir
                toast.success("Redirigiendo a Mercado Pago...");
                window.location.href = orderResponse.paymentUrl;
            } else {
                toast.error("No se pudo obtener la URL de pago");
            }

        } catch (error: any) {
            console.error("Error creando orden:", error);
            toast.error(error.response?.data?.message || "Error al procesar el pago");
        } finally {
            setIsProcessing(false);
        }
    }

    if (isLoading || !user || !event || !selectedArea) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-8 max-w-4xl">
                <Button variant="ghost" asChild className="mb-6">
                    <Link href={`/event/${event.id}/select-seats`}>
                        <ArrowLeft className="mr-2 h-4 w-4" />
                        Volver a selección
                    </Link>
                </Button>

                <h1 className="text-2xl md:text-3xl font-bold mb-8">Finalizar Compra</h1>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* Payment Form */}
                    <div className="md:col-span-2">
                        <form onSubmit={handleCreateOrder} className="space-y-6">
                            <Card>
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <CreditCard className="h-5 w-5" />
                                        Información de Contacto
                                    </CardTitle>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="space-y-2">
                                        <Label htmlFor="email">Email</Label>
                                        <Input 
                                            id="email" 
                                            type="email" 
                                            defaultValue={user.email} 
                                            disabled
                                            className="bg-muted"
                                        />
                                    </div>

                                    <div className="space-y-2">
                                        <Label htmlFor="firstName">Nombre</Label>
                                        <Input 
                                            id="firstName" 
                                            defaultValue={user.firstName || ""} 
                                            disabled
                                            className="bg-muted"
                                        />
                                    </div>

                                    <div className="space-y-2">
                                        <Label htmlFor="lastName">Apellido</Label>
                                        <Input 
                                            id="lastName" 
                                            defaultValue={user.lastName || ""} 
                                            disabled
                                            className="bg-muted"
                                        />
                                    </div>

                                    <div className="space-y-2">
                                        <Label htmlFor="phone">Teléfono</Label>
                                        <Input
                                            id="phone"
                                            type="tel"
                                            placeholder="+54 11 1234-5678"
                                            value={phone}
                                            onChange={(e) => {
                                                setPhone(e.target.value)
                                                if (errors.phone) setErrors((prev) => ({ ...prev, phone: "" }))
                                            }}
                                            required
                                        />
                                        {errors.phone && <p className="text-sm text-destructive">{errors.phone}</p>}
                                    </div>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader>
                                    <CardTitle>Método de Pago</CardTitle>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="text-sm text-muted-foreground mb-4">
                                        Serás redirigido a Mercado Pago para completar tu pago de forma segura.
                                    </div>

                                    {/* Contenedor para el botón de Mercado Pago */}
                                    {preferenceId && (
                                        <div id="wallet-container" className="min-h-[48px]"></div>
                                    )}

                                    <StarBorder className='w-full'>
                                        <Button 
                                            type="submit" 
                                            className="w-full gradient-brand text-white" 
                                            size="lg" 
                                            disabled={isProcessing}
                                        >
                                            {isProcessing ? (
                                                <>
                                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                                    Procesando...
                                                </>
                                            ) : (
                                                <>
                                                    Continuar con el pago
                                                </>
                                            )}
                                        </Button>
                                    </StarBorder>
                                </CardContent>
                            </Card>

                            <div className="flex items-center justify-center gap-2 text-xs text-muted-foreground">
                                <svg className="h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                                    <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                                </svg>
                                <span>Pago seguro procesado por Mercado Pago</span>
                            </div>
                        </form>
                    </div>

                    {/* Order Summary */}
                    <div className="md:col-span-1">
                        <Card className="sticky top-20">
                            <CardHeader>
                                <CardTitle>Resumen del Pedido</CardTitle>
                            </CardHeader>
                            <CardContent className="space-y-4">
                                <div>
                                    <p className="font-semibold mb-1">{event.title}</p>
                                    <p className="text-sm text-muted-foreground">
                                        {new Date(event.date).toLocaleDateString("es-ES")} - {event.time}
                                    </p>
                                </div>

                                <div className="space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Área</span>
                                        <span className="font-medium">{selectedArea.name}</span>
                                    </div>

                                    {selectedArea.type === "general" ? (
                                        <div className="flex justify-between">
                                            <span className="text-muted-foreground">Cantidad</span>
                                            <span className="font-medium">
                                                {quantity} entrada{Number(quantity) > 1 ? "s" : ""}
                                            </span>
                                        </div>
                                    ) : (
                                        <div>
                                            <div className="flex justify-between mb-1">
                                                <span className="text-muted-foreground">Asientos</span>
                                                <span className="font-medium">{parsedSeats.length}</span>
                                            </div>
                                            {parsedSeats.length <= 3 ? (
                                                <div className="space-y-1 pl-4">
                                                    {parsedSeats.map((seat: { row: string; seat: number }, idx: number) => (
                                                        <p key={idx} className="text-xs text-muted-foreground">
                                                            Fila {seat.row} - Asiento {seat.seat}
                                                        </p>
                                                    ))}
                                                </div>
                                            ) : (
                                                <Collapsible>
                                                    <div className="space-y-1 pl-4">
                                                        {parsedSeats.slice(0, 2).map((seat: { row: string; seat: number }, idx: number) => (
                                                            <p key={idx} className="text-xs text-muted-foreground">
                                                                Fila {seat.row} - Asiento {seat.seat}
                                                            </p>
                                                        ))}
                                                    </div>
                                                    <CollapsibleTrigger asChild>
                                                        <Button variant="ghost" size="sm" className="h-6 text-xs pl-4 mt-1">
                                                            Ver todos
                                                            <ChevronDown className="ml-1 h-3 w-3" />
                                                        </Button>
                                                    </CollapsibleTrigger>
                                                    <CollapsibleContent className="space-y-1 pl-4 mt-1">
                                                        {parsedSeats.slice(2).map((seat: { row: string; seat: number }, idx: number) => (
                                                            <p key={idx} className="text-xs text-muted-foreground">
                                                                Fila {seat.row} - Asiento {seat.seat}
                                                            </p>
                                                        ))}
                                                    </CollapsibleContent>
                                                </Collapsible>
                                            )}
                                        </div>
                                    )}

                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Precio unitario</span>
                                        <span className="font-medium">${selectedArea.price.toLocaleString()}</span>
                                    </div>
                                </div>

                                <div className="pt-4 border-t space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Subtotal</span>
                                        <span className="font-medium">${total.toLocaleString()}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Cargo por servicio</span>
                                        <span className="font-medium">${serviceFee.toLocaleString()}</span>
                                    </div>
                                </div>

                                <div className="pt-4 border-t">
                                    <div className="flex justify-between text-lg font-bold">
                                        <span>Total</span>
                                        <GradientText>
                                            <span className='font-bold'>${finalTotal.toLocaleString()}</span>
                                        </GradientText>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </main>
        </div>
    )
}
