"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter, useParams, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { ArrowLeft, Loader2, ChevronDown, CreditCard, Tag, X, Check, ExternalLink, Clock } from "lucide-react"
import Link from "next/link"
import { useAuth } from '@/hooks/auth/useAuth'
import { useEvent } from '@/hooks/event/useEvent'
import { Navbar } from '@/components/Navbar'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'
import { orderService } from '@/services/orderService'
import { couponService } from '@/services/couponService'
import type { CreateOrderRequest } from '@/types/Order'
import { toast } from 'sonner'

export default function CheckoutPage() {
    const router = useRouter()
    const params = useParams()
    const searchParams = useSearchParams()
    const { user, isLoading } = useAuth()
    const { data: event, isLoading: isLoadingEvent } = useEvent(params.id as string)
    const [isProcessing, setIsProcessing] = useState(false)
    const [phone, setPhone] = useState("")
    const [waitingPayment, setWaitingPayment] = useState(false)
    const [paymentOrderId, setPaymentOrderId] = useState<string | null>(null)
    const [paymentUrl, setPaymentUrl] = useState<string | null>(null)
    const [paymentWindow, setPaymentWindow] = useState<Window | null>(null)
    const [errors, setErrors] = useState<Record<string, string>>({})
    const [couponCode, setCouponCode] = useState("")
    const [couponDiscount, setCouponDiscount] = useState(0)
    const [couponApplied, setCouponApplied] = useState<string | null>(null)
    const [couponLoading, setCouponLoading] = useState(false)
    const [couponError, setCouponError] = useState("")

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
    const finalTotal = total + serviceFee - couponDiscount

    const handleApplyCoupon = async () => {
        if (!couponCode.trim()) {
            setCouponError("Ingresá un código de cupón")
            return
        }

        setCouponLoading(true)
        setCouponError("")

        try {
            // Enviar subtotal en centavos reales (total y serviceFee están en pesos, multiplicar por 100)
            const subtotalCents = (total + serviceFee) * 100
            const response = await couponService.validateCoupon(
                couponCode.toUpperCase(),
                event?.id || "",
                subtotalCents,
                event?.organizerId || event?.id || "",
                user?.id
            )

            if (response.valid && response.discountCents) {
                // discountCents viene en centavos reales, convertir a pesos para uso en UI
                const discountPesos = Math.round(response.discountCents / 100)
                setCouponDiscount(discountPesos)
                setCouponApplied(couponCode.toUpperCase())
                setCouponError("")
                toast.success(`Cupón aplicado: -$${discountPesos.toLocaleString("es-AR")}`)
            } else {
                setCouponError(response.errorMessage || "Cupón no válido")
                setCouponDiscount(0)
                setCouponApplied(null)
            }
        } catch (error: any) {
            console.error("Error validating coupon:", error)
            const msg = error.response?.data?.errorMessage || error.response?.data?.message || "No se pudo validar el cupón"
            setCouponError(msg)
            setCouponDiscount(0)
            setCouponApplied(null)
        } finally {
            setCouponLoading(false)
        }
    }

    const handleRemoveCoupon = () => {
        setCouponCode("")
        setCouponDiscount(0)
        setCouponApplied(null)
        setCouponError("")
        toast.info("Cupón removido")
    }

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    // Polling del estado del pago cuando se abre la pestaña de MP
    useEffect(() => {
        if (!waitingPayment || !paymentOrderId) return;

        const pollInterval = setInterval(async () => {
            try {
                const data = await orderService.getPaymentStatus(paymentOrderId);
                
                if (data.status === 'CAPTURED') {
                    clearInterval(pollInterval);
                    toast.success("Pago confirmado. Redirigiendo a tus tickets...");
                    router.push("/my-tickets");
                } else if (data.status === 'FAILED' || data.status === 'CANCELED') {
                    clearInterval(pollInterval);
                    setWaitingPayment(false);
                    setIsProcessing(false);
                    toast.error("El pago fue rechazado o cancelado. Podés intentar nuevamente.");
                }
            } catch (error) {
                console.error("Error consultando estado del pago:", error);
            }
        }, 4000);

        // Timeout de 10 minutos
        const timeout = setTimeout(() => {
            clearInterval(pollInterval);
            setWaitingPayment(false);
            setIsProcessing(false);
            toast.error("Tiempo de espera agotado. Si ya pagaste, revisá en 'Mis Tickets'.");
        }, 600000);

        return () => {
            clearInterval(pollInterval);
            clearTimeout(timeout);
        };
    }, [waitingPayment, paymentOrderId, router]);

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

            // Validar teléfono
            if (!phone || phone.trim().length < 8) {
                toast.error("Por favor, ingresa un número de teléfono válido.");
                return;
            }

            // Validar que tenemos un areaId válido (debe ser un UUID string)
            if (!areaId || areaId === "undefined" || areaId === "null" || areaId.trim() === "") {
                toast.error("Error: No se ha seleccionado un área válida. Por favor, vuelve a la selección de asientos.");
                setIsProcessing(false);
                return;
            }
            
            // El eventId puede ser UUID o número - intentamos convertir si es posible
            const eventId = event.id; // Mantener como string (UUID) o número según corresponda
            
            const items = parsedSeats.length > 0
                ? parsedSeats.map((seat: { row: string; seat: number }) => {
                    // Para asientos numerados, generamos un identificador único
                    // El backend espera un UUID o string, así que creamos un formato único
                    const seatId = `${seat.row}-${seat.seat}`;
                    
                    return {
                        eventId: eventId,
                        venueAreaId: areaId, // UUID como string
                        venueSeatId: seatId, // String con formato "FILA-ASIENTO"
                        ticketTypeId: 1, // 1 = adulto estándar
                        unitPriceCents: selectedArea.priceCents + Math.round(selectedArea.priceCents * 0.1),
                        quantity: 1,
                    };
                })
                : [{
                    // Área general (sin asiento específico)
                    eventId: eventId,
                    venueAreaId: areaId, // UUID como string
                    venueSeatId: undefined,
                    ticketTypeId: 1, // 1 = adulto estándar
                    unitPriceCents: selectedArea.priceCents,
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
                organizerId: event.organizerId || event.id, // Usar organizerId del evento o el eventId como fallback
                items: items,
                currency: "ARS",
                paymentDescription: `Entradas para ${event.title}`,
                notes: `Compra de ${items.length} entrada(s) para ${event.title} - ${selectedArea.name}`,
                ...(couponApplied && couponCode ? { couponCode: couponCode.toUpperCase() } : {}),
            };

            // Crear la orden y obtener la URL de pago
            const orderResponse = await orderService.createOrder(orderRequest);

            if (orderResponse.paymentUrl) {
                // Abrir Mercado Pago en una nueva pestaña
                const mpWindow = window.open(orderResponse.paymentUrl, '_blank');
                
                if (mpWindow) {
                    // La pestaña se abrió correctamente
                    setPaymentWindow(mpWindow);
                    setPaymentOrderId(orderResponse.id);
                    setPaymentUrl(orderResponse.paymentUrl);
                    setWaitingPayment(true);
                    toast.info("Se abrió Mercado Pago en una nueva pestaña. Completá el pago allí.");
                } else {
                    // El navegador bloqueó el popup, fallback a redirección directa
                    toast.info("Redirigiendo a Mercado Pago...");
                    window.location.href = orderResponse.paymentUrl;
                }
            } else {
                toast.error("No se pudo obtener la URL de pago");
            }

        } catch (error: any) {
            console.error("Error creando orden:", error);
            console.error("API Error:", error.response?.data);
            
            const errorMessage = error.response?.data?.message || error.message || "";
            
            if (error.response?.status === 401) {
                toast.error("Error de autenticación. Por favor, inicia sesión nuevamente.");
            } else if (error.response?.status === 409) {
                toast.error("Lo sentimos, el asiento seleccionado ya no está disponible. Por favor, selecciona otro asiento.");
            } else if (error.response?.status === 400) {
                if (errorMessage.toLowerCase().includes("seat") || errorMessage.toLowerCase().includes("reserved")) {
                    toast.error("El asiento seleccionado ya está reservado. Por favor, elige otro asiento.");
                } else if (errorMessage.toLowerCase().includes("sold out") || errorMessage.toLowerCase().includes("capacity")) {
                    toast.error("No hay más entradas disponibles para este evento.");
                } else {
                    toast.error(errorMessage || "Hay un problema con los datos ingresados. Por favor, verifica la información.");
                }
            } else if (error.response?.status === 500) {
                if (errorMessage.toLowerCase().includes("cupón") || errorMessage.toLowerCase().includes("coupon")) {
                    toast.error("Error al aplicar el cupón. Intentá de nuevo sin cupón o con otro código.");
                } else if (errorMessage.toLowerCase().includes("payment") || errorMessage.toLowerCase().includes("pago")) {
                    toast.error("Error al procesar el pago. Por favor, intentá nuevamente.");
                } else {
                    toast.error("Error interno del servidor. Por favor, intentá nuevamente en unos momentos.");
                }
            } else {
                toast.error("Error de conexión. Verificá tu conexión a internet e intentá nuevamente.");
            }
        } finally {
            setIsProcessing(false);
        }
    }

    if (isLoading || isLoadingEvent || !user || !event || !selectedArea) {
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

                {/* Estado de espera de pago */}
                {waitingPayment ? (
                    <Card className="max-w-2xl mx-auto">
                        <CardContent className="pt-12 pb-8 space-y-6 text-center">
                            <div className="flex justify-center">
                                <div className="relative">
                                    <Clock className="h-20 w-20 text-primary animate-pulse" />
                                </div>
                            </div>

                            <div>
                                <h2 className="text-2xl font-bold mb-2">Esperando confirmaci&oacute;n de pago...</h2>
                                <p className="text-muted-foreground">
                                    Complet&aacute; el pago en la pesta&ntilde;a de Mercado Pago que se abri&oacute;.
                                </p>
                            </div>

                            <div className="bg-muted/50 rounded-lg p-6 space-y-3">
                                <div className="flex items-center justify-center gap-2">
                                    <Loader2 className="h-4 w-4 animate-spin text-primary" />
                                    <p className="text-sm text-muted-foreground">
                                        Verificando el estado del pago autom&aacute;ticamente...
                                    </p>
                                </div>
                                <p className="text-xs text-muted-foreground">
                                    Esta p&aacute;gina se actualizar&aacute; sola cuando se confirme el pago.
                                </p>
                            </div>

                            <div className="flex flex-col sm:flex-row gap-3 justify-center pt-2">
                                <Button
                                    variant="outline"
                                    onClick={() => {
                                        if (paymentWindow && !paymentWindow.closed) {
                                            paymentWindow.focus();
                                        } else if (paymentUrl) {
                                            const newWindow = window.open(paymentUrl, '_blank');
                                            if (newWindow) setPaymentWindow(newWindow);
                                        }
                                    }}
                                    className="gap-2"
                                >
                                    <ExternalLink className="h-4 w-4" />
                                    Ir a la pesta&ntilde;a de Mercado Pago
                                </Button>
                                <Button
                                    variant="ghost"
                                    onClick={() => {
                                        setWaitingPayment(false);
                                        setIsProcessing(false);
                                    }}
                                >
                                    Cancelar
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                ) : (

                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* Payment Form */}
                    <div className="md:col-span-2">
                        <form onSubmit={handleCreateOrder} className="space-y-6">
                            <Card>
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <CreditCard className="h-5 w-5" />
                                        Informaci&oacute;n de Contacto
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
                                        <Label htmlFor="phone">Tel&eacute;fono</Label>
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
                                    <CardTitle>M&eacute;todo de Pago</CardTitle>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="text-sm text-muted-foreground mb-4">
                                        Se abrir&aacute; Mercado Pago en una nueva pesta&ntilde;a para completar tu pago de forma segura.
                                    </div>

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
                                        {new Date(event.startsAt).toLocaleDateString("es-ES")} - {new Date(event.startsAt).toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" })}
                                    </p>
                                </div>

                                <div className="space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Área</span>
                                        <span className="font-medium">{selectedArea.name}</span>
                                    </div>

                                    {selectedArea.isGeneralAdmission ? (
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
                                        <span className="font-medium">{selectedArea.currency}${(selectedArea.priceCents / 100).toLocaleString()}</span>
                                    </div>
                                </div>

                                {/* Cupón de descuento */}
                                <div className="pt-4 border-t space-y-3">
                                    <p className="text-sm font-medium flex items-center gap-1.5">
                                        <Tag className="h-3.5 w-3.5" />
                                        Cupón de descuento
                                    </p>
                                    {couponApplied ? (
                                        <div className="flex items-center justify-between bg-green-50 border border-green-200 rounded-md px-3 py-2">
                                            <div className="flex items-center gap-2">
                                                <Check className="h-4 w-4 text-green-600" />
                                                <span className="text-sm font-mono font-bold text-green-700">{couponApplied}</span>
                                            </div>
                                            <Button
                                                type="button"
                                                variant="ghost"
                                                size="icon"
                                                className="h-6 w-6"
                                                onClick={handleRemoveCoupon}
                                            >
                                                <X className="h-3.5 w-3.5 text-muted-foreground" />
                                            </Button>
                                        </div>
                                    ) : (
                                        <div className="flex gap-2">
                                            <Input
                                                placeholder="Código del cupón"
                                                value={couponCode}
                                                onChange={(e) => {
                                                    setCouponCode(e.target.value.toUpperCase())
                                                    setCouponError("")
                                                }}
                                                onKeyDown={(e) => {
                                                    if (e.key === "Enter") {
                                                        e.preventDefault()
                                                        handleApplyCoupon()
                                                    }
                                                }}
                                                className="text-sm font-mono"
                                                disabled={couponLoading}
                                            />
                                            <Button
                                                type="button"
                                                variant="outline"
                                                size="sm"
                                                onClick={handleApplyCoupon}
                                                disabled={couponLoading || !couponCode.trim()}
                                                className="shrink-0"
                                            >
                                                {couponLoading ? (
                                                    <Loader2 className="h-4 w-4 animate-spin" />
                                                ) : (
                                                    "Aplicar"
                                                )}
                                            </Button>
                                        </div>
                                    )}
                                    {couponError && (
                                        <p className="text-xs text-destructive">{couponError}</p>
                                    )}
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
                                    {couponDiscount > 0 && (
                                        <div className="flex justify-between text-green-600">
                                            <span>Descuento ({couponApplied})</span>
                                            <span className="font-medium">-${couponDiscount.toLocaleString()}</span>
                                        </div>
                                    )}
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
                )}
            </main>
        </div>
    )
}
