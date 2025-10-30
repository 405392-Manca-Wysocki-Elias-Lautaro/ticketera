"use client"

import { useEffect, useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { useAuth } from "@/hooks/auth/useAuth"
import { Navbar } from "@/components/Navbar"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft } from "lucide-react"
import Link from "next/link"
import { mockEvents } from '@/mocks/mockEvents'

export default function SelectSeatsPage() {
  const router = useRouter()
  const params = useParams()
  const { user, isLoading } = useAuth()
  const [event, setEvent] = useState(mockEvents.find((e) => e.id === params.id))
  // TODO: Añadir tipo Area
  const [selectedArea, setSelectedArea] = useState<any | null>(null)
  // TODO: Añadir tipo Row
  const [selectedRow, setSelectedRow] = useState<any | null>(null)
  const [selectedSeats, setSelectedSeats] = useState<number[]>([])
  const [quantity, setQuantity] = useState(1)

  useEffect(() => {
    if (!isLoading && !user) {
      router.push("/login")
    }
  }, [user, isLoading, router])

  if (isLoading || !user || !event) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  //TODO: Añadir tipo Area
  const handleAreaSelect = (area: any) => {
    setSelectedArea(area)
    setSelectedRow(null)
    setSelectedSeats([])
  }

  const handleSeatToggle = (seatNumber: number) => {
    setSelectedSeats((prev) => {
      if (prev.includes(seatNumber)) {
        return prev.filter((s) => s !== seatNumber)
      } else {
        return [...prev, seatNumber]
      }
    })
  }

  const handleContinue = () => {
    const total = selectedArea
      ? selectedArea.type === "general"
        ? selectedArea.price * quantity
        : selectedArea.price * selectedSeats.length
      : 0

    router.push(
      `/event/${event.id}/checkout?area=${selectedArea?.id}&seats=${selectedSeats.join(",")}&quantity=${quantity}&total=${total}`,
    )
  }

  const canContinue = selectedArea && (selectedArea.type === "general" ? quantity > 0 : selectedSeats.length > 0)

  const total = selectedArea
    ? selectedArea.type === "general"
      ? selectedArea.price * quantity
      : selectedArea.price * selectedSeats.length
    : 0

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <main className="container mx-auto px-4 py-8">
        <Button variant="ghost" asChild className="mb-6">
          <Link href={`/event/${event.id}`}>
            <ArrowLeft className="mr-2 h-4 w-4" />
            Volver al evento
          </Link>
        </Button>

        <h1 className="text-2xl md:text-3xl font-bold mb-2">{event.title}</h1>
        <p className="text-muted-foreground mb-8">Selecciona tu área y asientos</p>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Selection Area */}
          <div className="lg:col-span-2 space-y-6">
            {/* Step 1: Select Area */}
            <Card>
              <CardHeader>
                <CardTitle>1. Selecciona un Área</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {event.areas.map((area) => (
                  <button
                    key={area.id}
                    onClick={() => handleAreaSelect(area)}
                    className={`w-full p-4 rounded-lg border-2 transition-all text-left ${
                      selectedArea?.id === area.id
                        ? "border-primary bg-primary/5"
                        : "border-border hover:border-primary/50"
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <h3 className="font-semibold">{area.name}</h3>
                        <p className="text-sm text-muted-foreground">
                          {area.type === "general" ? "Admisión General" : "Asientos Numerados"}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="text-lg font-bold gradient-text">${area.price.toLocaleString()}</p>
                        <p className="text-xs text-muted-foreground">{area.capacity} lugares</p>
                      </div>
                    </div>
                  </button>
                ))}
              </CardContent>
            </Card>

            {/* Step 2: Select Seats or Quantity */}
            {selectedArea && (
              <Card>
                <CardHeader>
                  <CardTitle>
                    2. {selectedArea.type === "general" ? "Cantidad de Entradas" : "Selecciona tus Asientos"}
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  {selectedArea.type === "general" ? (
                    <div className="space-y-4">
                      <div className="flex items-center gap-4">
                        <Button variant="outline" size="icon" onClick={() => setQuantity(Math.max(1, quantity - 1))}>
                          -
                        </Button>
                        <div className="flex-1 text-center">
                          <p className="text-3xl font-bold">{quantity}</p>
                          <p className="text-sm text-muted-foreground">entradas</p>
                        </div>
                        <Button variant="outline" size="icon" onClick={() => setQuantity(Math.min(10, quantity + 1))}>
                          +
                        </Button>
                      </div>
                    </div>
                  ) : (
                    <div className="space-y-4">
                      {selectedArea.rows?.map((row: any) => (
                        <div key={row.id} className="space-y-2">
                          <h4 className="font-medium">Fila {row.name}</h4>
                          <div className="grid grid-cols-10 gap-2">
                            {Array.from({ length: row.endSeat - row.startSeat + 1 }, (_, i) => row.startSeat + i).map(
                              (seatNumber) => {
                                const isSelected = selectedSeats.includes(seatNumber)
                                const isOccupied = Math.random() > 0.7 // Mock occupied seats

                                return (
                                  <button
                                    key={seatNumber}
                                    onClick={() => !isOccupied && handleSeatToggle(seatNumber)}
                                    disabled={isOccupied}
                                    className={`aspect-square rounded text-xs font-medium transition-all ${
                                      isOccupied
                                        ? "bg-muted text-muted-foreground cursor-not-allowed"
                                        : isSelected
                                          ? "bg-primary text-primary-foreground"
                                          : "bg-secondary hover:bg-secondary/80"
                                    }`}
                                  >
                                    {seatNumber}
                                  </button>
                                )
                              },
                            )}
                          </div>
                        </div>
                      ))}

                      <div className="flex items-center gap-4 pt-4 text-sm">
                        <div className="flex items-center gap-2">
                          <div className="h-6 w-6 rounded bg-secondary" />
                          <span>Disponible</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="h-6 w-6 rounded bg-primary" />
                          <span>Seleccionado</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="h-6 w-6 rounded bg-muted" />
                          <span>Ocupado</span>
                        </div>
                      </div>
                    </div>
                  )}
                </CardContent>
              </Card>
            )}
          </div>

          {/* Summary Sidebar */}
          <div className="lg:col-span-1">
            <Card className="sticky top-20">
              <CardHeader>
                <CardTitle>Resumen</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {selectedArea ? (
                  <>
                    <div>
                      <p className="text-sm text-muted-foreground mb-1">Área seleccionada</p>
                      <p className="font-semibold">{selectedArea.name}</p>
                      <Badge variant="secondary" className="mt-1">
                        {selectedArea.type === "general" ? "Admisión General" : "Numerado"}
                      </Badge>
                    </div>

                    {selectedArea.type === "general" ? (
                      <div>
                        <p className="text-sm text-muted-foreground mb-1">Cantidad</p>
                        <p className="font-semibold">
                          {quantity} entrada{quantity > 1 ? "s" : ""}
                        </p>
                      </div>
                    ) : (
                      selectedSeats.length > 0 && (
                        <div>
                          <p className="text-sm text-muted-foreground mb-1">Asientos seleccionados</p>
                          <p className="font-semibold">{selectedSeats.sort((a, b) => a - b).join(", ")}</p>
                        </div>
                      )
                    )}

                    <div className="pt-4 border-t">
                      <div className="flex justify-between mb-2">
                        <span className="text-muted-foreground">Subtotal</span>
                        <span className="font-semibold">${total.toLocaleString()}</span>
                      </div>
                      <div className="flex justify-between text-lg font-bold">
                        <span>Total</span>
                        <span className="gradient-text">${total.toLocaleString()}</span>
                      </div>
                    </div>

                    <Button
                      className="w-full gradient-brand text-white"
                      size="lg"
                      disabled={!canContinue}
                      onClick={handleContinue}
                    >
                      Continuar al Pago
                    </Button>
                  </>
                ) : (
                  <p className="text-sm text-muted-foreground text-center py-8">Selecciona un área para continuar</p>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  )
}
