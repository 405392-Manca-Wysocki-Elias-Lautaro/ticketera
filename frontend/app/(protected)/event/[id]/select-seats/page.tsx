"use client"

import { useEffect, useState, useMemo } from "react"
import { useRouter, useParams, useSearchParams } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { ArrowLeft, ChevronDown } from "lucide-react"
import Link from "next/link"
import { useAuth } from '@/hooks/auth/useAuth'
import { Navbar } from '@/components/Navbar'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'
import { EventArea } from '@/types/EventArea'
import { toast } from 'sonner'
import { useEvent } from '@/hooks/event/useEvent'

export default function SelectSeatsPage() {
    const router = useRouter();
    const params = useParams();
    const id = params.id;
    const searchParams = useSearchParams()
    const { user, isLoading } = useAuth()

    const {data: event, isLoading: isLoadingEvent} = useEvent(Array.isArray(id) ? id[0] : id);

    const urlAreaId = searchParams.get("area")
    const urlSeats = searchParams.get("seats")
    const urlQuantity = searchParams.get("quantity")

    const [selectedArea, setSelectedArea] = useState<EventArea | null>(() => {
        if (urlAreaId && event) {
            return event.areas.find((a) => a.id === urlAreaId) || null
        }
        return null
    })

    const [selectedSeats, setSelectedSeats] = useState<{ row: string; seat: number }[]>(() => {
        if (urlSeats) {
            return urlSeats.split(",").map((seat: string) => {
                const [row, seatNum] = seat.split("-")
                return { row, seat: Number.parseInt(seatNum) }
            })
        }
        return []
    })

    const [quantity, setQuantity] = useState(() => {
        return urlQuantity ? Number.parseInt(urlQuantity) : 1
    })

    const occupiedSeats = useMemo(() => {
        if (!selectedArea || selectedArea.isGeneralAdmission) return new Set<string>()

        // Para áreas numeradas, simular asientos ocupados
        // Nota: En el servicio real, esto debería venir del backend
        const occupied = new Set<string>()
        const totalSeats = selectedArea.totalSeats || 100
        const occupiedCount = Math.floor(totalSeats * 0.3)

        // Simular filas A, B, C con asientos del 1 al 20 cada una
        const rows = ['A', 'B', 'C', 'D', 'E']
        for (let i = 0; i < occupiedCount; i++) {
            const randomRow = rows[Math.floor(Math.random() * rows.length)]
            const randomSeat = Math.floor(Math.random() * 20) + 1
            occupied.add(`${randomRow}-${randomSeat}`)
        }

        return occupied
    }, [selectedArea]) // Only regenerate when area changes

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    if (isLoading || !user || !event || isLoadingEvent) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    const handleAreaSelect = (area: EventArea) => {
        setSelectedArea(area)
        setSelectedSeats([])
    }

    const handleSeatToggle = (row: string, seatNumber: number) => {
        setSelectedSeats((prev) => {
            const exists = prev.find((s) => s.row === row && s.seat === seatNumber)

            if (exists) {
                return prev.filter((s) => !(s.row === row && s.seat === seatNumber))
            } else {
                return [...prev, { row, seat: seatNumber }]
            }
        })
    }

    const handleContinue = () => {
        if (!selectedArea) {
            toast.error("Por favor, selecciona un área antes de continuar.");
            return;
        }

        const total = selectedArea.isGeneralAdmission
            ? (selectedArea.priceCents / 100) * quantity
            : (selectedArea.priceCents / 100) * selectedSeats.length

        const seatsParam = selectedSeats.map((s) => `${s.row}-${s.seat}`).join(",")

        router.push(
            `/event/${event?.id}/checkout?area=${selectedArea.id}&seats=${seatsParam}&quantity=${quantity}&total=${total}`,
        )
    }

    const canContinue = selectedArea && (selectedArea.isGeneralAdmission ? quantity > 0 : selectedSeats.length > 0)

    const total = selectedArea
        ? selectedArea.isGeneralAdmission
            ? (selectedArea.priceCents / 100) * quantity
            : (selectedArea.priceCents / 100) * selectedSeats.length
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

                <h1 className="text-2xl md:text-3xl font-bold mb-2">{event.eventTitle}</h1>
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
                                {event.areas?.map((area) => (
                                    <button
                                        key={area.id}
                                        onClick={() => handleAreaSelect(area)}
                                        className={`w-full p-4 rounded-lg border-2 transition-all text-left ${selectedArea?.id === area.id
                                            ? "border-primary bg-primary/5"
                                            : "border-border hover:border-primary/50"
                                            }`}
                                    >
                                        <div className="flex items-center justify-between">
                                            <div>
                                                <h3 className="font-semibold">{area.name}</h3>
                                                <p className="text-sm text-muted-foreground">
                                                    {area.isGeneralAdmission ? "Admisión General" : "Asientos Numerados"}
                                                </p>
                                            </div>
                                            <div className="text-right">
                                                <GradientText>
                                                    <p className="text-lg font-bold">{area.currency}${(area.priceCents / 100).toLocaleString()}</p>
                                                </GradientText>
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
                                        2. {selectedArea.isGeneralAdmission ? "Cantidad de Entradas" : "Selecciona tus Asientos"}
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    {selectedArea.isGeneralAdmission ? (
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
                                            {/* Simular filas para áreas numeradas */}
                                            {['A', 'B', 'C', 'D', 'E'].map((rowName) => (
                                                <div key={rowName} className="space-y-2">
                                                    <h4 className="font-medium">Fila {rowName}</h4>
                                                    <div className="grid grid-cols-10 gap-2">
                                                        {Array.from({ length: 20 }, (_, i) => i + 1).map(
                                                            (seatNumber) => {
                                                                const seatKey = `${rowName}-${seatNumber}`
                                                                const isSelected = selectedSeats.some(
                                                                    (s) => s.row === rowName && s.seat === seatNumber,
                                                                )
                                                                const isOccupied = occupiedSeats.has(seatKey)

                                                                return (
                                                                    <button
                                                                        key={seatNumber}
                                                                        onClick={() => !isOccupied && handleSeatToggle(rowName, seatNumber)}
                                                                        disabled={isOccupied}
                                                                        className={`aspect-square rounded text-xs font-medium transition-all ${isOccupied
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
                                                {selectedArea.isGeneralAdmission ? "Admisión General" : "Numerado"}
                                            </Badge>
                                        </div>

                                        {selectedArea.isGeneralAdmission ? (
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
                                                    {selectedSeats.length <= 5 ? (
                                                        <div className="space-y-1">
                                                            {selectedSeats
                                                                .sort((a, b) => a.row.localeCompare(b.row) || a.seat - b.seat)
                                                                .map((seat, idx) => (
                                                                    <p key={idx} className="font-semibold text-sm">
                                                                        Fila {seat.row} - Asiento {seat.seat}
                                                                    </p>
                                                                ))}
                                                        </div>
                                                    ) : (
                                                        <Collapsible>
                                                            <div className="space-y-1">
                                                                {selectedSeats
                                                                    .sort((a, b) => a.row.localeCompare(b.row) || a.seat - b.seat)
                                                                    .slice(0, 3)
                                                                    .map((seat, idx) => (
                                                                        <p key={idx} className="font-semibold text-sm">
                                                                            Fila {seat.row} - Asiento {seat.seat}
                                                                        </p>
                                                                    ))}
                                                            </div>
                                                            <CollapsibleTrigger asChild>
                                                                <Button variant="ghost" size="sm" className="w-full mt-2">
                                                                    Ver todos ({selectedSeats.length})
                                                                    <ChevronDown className="ml-2 h-4 w-4" />
                                                                </Button>
                                                            </CollapsibleTrigger>
                                                            <CollapsibleContent className="space-y-1 mt-2">
                                                                {selectedSeats
                                                                    .sort((a, b) => a.row.localeCompare(b.row) || a.seat - b.seat)
                                                                    .slice(3)
                                                                    .map((seat, idx) => (
                                                                        <p key={idx} className="font-semibold text-sm">
                                                                            Fila {seat.row} - Asiento {seat.seat}
                                                                        </p>
                                                                    ))}
                                                            </CollapsibleContent>
                                                        </Collapsible>
                                                    )}
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
                                                <GradientText>
                                                    <span className='font-bold'>${total.toLocaleString()}</span>
                                                </GradientText>
                                            </div>
                                        </div>

                                        <StarBorder className='w-full'>
                                            <Button
                                                className="w-full gradient-brand text-white"
                                                size="lg"
                                                disabled={!canContinue}
                                                onClick={handleContinue}
                                            >
                                                Continuar al Pago
                                            </Button>
                                        </StarBorder>
                                    </>
                                ) : (
                                    <p className="text-sm text-muted-foreground text-center py-8">Elige tu entrada para continuar</p>
                                )}
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </main>
        </div>
    )
}
