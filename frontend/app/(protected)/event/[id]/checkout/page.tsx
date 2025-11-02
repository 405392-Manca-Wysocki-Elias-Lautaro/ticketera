"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter, useParams, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { ArrowLeft, CreditCard, Lock, ChevronDown } from "lucide-react"
import Link from "next/link"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockEvents } from '@/mocks/mockEvents'
import { Navbar } from '@/components/Navbar'

export default function CheckoutPage() {
  const router = useRouter()
  const params = useParams()
  const searchParams = useSearchParams()
  const { user, isLoading } = useAuth()
  const [event, setEvent] = useState(mockEvents.find((e) => e.id === params.id))
  const [isProcessing, setIsProcessing] = useState(false)

  const [cardNumber, setCardNumber] = useState("")
  const [expiry, setExpiry] = useState("")
  const [cvv, setCvv] = useState("")
  const [phone, setPhone] = useState("")
  const [errors, setErrors] = useState<Record<string, string>>({})

  const areaId = searchParams.get("area")
  const seats = searchParams.get("seats")
  const quantity = searchParams.get("quantity")
  const total = Number(searchParams.get("total"))

  const selectedArea = event?.areas.find((a) => a.id === areaId)

  const parsedSeats = seats
    ? seats.split(",").map((s) => {
        const [row, seat] = s.split("-")
        return { row, seat: Number(seat) }
      })
    : []

  const serviceFee = Math.round(total * 0.1)
  const finalTotal = total + serviceFee

  useEffect(() => {
    if (!isLoading && !user) {
      router.push("/login")
    }
  }, [user, isLoading, router])

  const validateCardNumber = (value: string) => {
    const cleaned = value.replace(/\s/g, "")
    if (cleaned.length !== 16 || !/^\d+$/.test(cleaned)) {
      return "Número de tarjeta inválido (16 dígitos)"
    }
    return ""
  }

  const validateExpiry = (value: string) => {
    if (!/^\d{2}\/\d{2}$/.test(value)) {
      return "Formato inválido (MM/AA)"
    }
    const [month, year] = value.split("/").map(Number)
    if (month < 1 || month > 12) {
      return "Mes inválido"
    }
    const currentYear = new Date().getFullYear() % 100
    const currentMonth = new Date().getMonth() + 1
    if (year < currentYear || (year === currentYear && month < currentMonth)) {
      return "Tarjeta vencida"
    }
    return ""
  }

  const validateCVV = (value: string) => {
    if (!/^\d{3,4}$/.test(value)) {
      return "CVV inválido (3-4 dígitos)"
    }
    return ""
  }

  const validatePhone = (value: string) => {
    const cleaned = value.replace(/[\s\-$$$$]/g, "")
    if (cleaned.length < 10 || !/^\+?\d+$/.test(cleaned)) {
      return "Teléfono inválido"
    }
    return ""
  }

  const handleCardNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/\s/g, "")
    if (value.length <= 16 && /^\d*$/.test(value)) {
      const formatted = value.match(/.{1,4}/g)?.join(" ") || value
      setCardNumber(formatted)
      if (errors.cardNumber) {
        setErrors((prev) => ({ ...prev, cardNumber: "" }))
      }
    }
  }

  const handleExpiryChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    let value = e.target.value.replace(/\D/g, "")
    if (value.length >= 2) {
      value = value.slice(0, 2) + "/" + value.slice(2, 4)
    }
    setExpiry(value)
    if (errors.expiry) {
      setErrors((prev) => ({ ...prev, expiry: "" }))
    }
  }

  const handlePayment = async (e: React.FormEvent) => {
    e.preventDefault()

    const newErrors: Record<string, string> = {}

    const cardError = validateCardNumber(cardNumber)
    if (cardError) newErrors.cardNumber = cardError

    const expiryError = validateExpiry(expiry)
    if (expiryError) newErrors.expiry = expiryError

    const cvvError = validateCVV(cvv)
    if (cvvError) newErrors.cvv = cvvError

    const phoneError = validatePhone(phone)
    if (phoneError) newErrors.phone = phoneError

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }

    setIsProcessing(true)

    // Simulate payment processing
    await new Promise((resolve) => setTimeout(resolve, 2000))

    // Redirect to success page
    router.push(`/event/${event?.id}/success`)
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
            <form onSubmit={handlePayment} className="space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <CreditCard className="h-5 w-5" />
                    Información de Pago
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="cardNumber">Número de Tarjeta</Label>
                    <Input
                      id="cardNumber"
                      placeholder="1234 5678 9012 3456"
                      value={cardNumber}
                      onChange={handleCardNumberChange}
                      maxLength={19}
                      required
                    />
                    {errors.cardNumber && <p className="text-sm text-destructive">{errors.cardNumber}</p>}
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="expiry">Vencimiento</Label>
                      <Input
                        id="expiry"
                        placeholder="MM/AA"
                        value={expiry}
                        onChange={handleExpiryChange}
                        maxLength={5}
                        required
                      />
                      {errors.expiry && <p className="text-sm text-destructive">{errors.expiry}</p>}
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="cvv">CVV</Label>
                      <Input
                        id="cvv"
                        placeholder="123"
                        value={cvv}
                        onChange={(e) => {
                          const value = e.target.value
                          if (/^\d*$/.test(value) && value.length <= 4) {
                            setCvv(value)
                            if (errors.cvv) setErrors((prev) => ({ ...prev, cvv: "" }))
                          }
                        }}
                        maxLength={4}
                        required
                      />
                      {errors.cvv && <p className="text-sm text-destructive">{errors.cvv}</p>}
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="cardName">Nombre en la Tarjeta</Label>
                    <Input id="cardName" placeholder="Juan Pérez" required />
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Información de Contacto</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input id="email" type="email" defaultValue={user.email} required />
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

              <Button type="submit" className="w-full gradient-brand text-white" size="lg" disabled={isProcessing}>
                {isProcessing ? (
                  <>
                    <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
                    Procesando...
                  </>
                ) : (
                  <>
                    <Lock className="mr-2 h-4 w-4" />
                    Pagar ${finalTotal.toLocaleString()}
                  </>
                )}
              </Button>

              <p className="text-xs text-center text-muted-foreground">
                Tu pago está protegido con encriptación de nivel bancario
              </p>
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
                          {parsedSeats.map((seat, idx) => (
                            <p key={idx} className="text-xs text-muted-foreground">
                              Fila {seat.row} - Asiento {seat.seat}
                            </p>
                          ))}
                        </div>
                      ) : (
                        <Collapsible>
                          <div className="space-y-1 pl-4">
                            {parsedSeats.slice(0, 2).map((seat, idx) => (
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
                            {parsedSeats.slice(2).map((seat, idx) => (
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
                    <span className="gradient-text">${finalTotal.toLocaleString()}</span>
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
