"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { QrCode, CheckCircle2, XCircle, Camera, Hash, ChevronDown, Calendar, MapPin, Users } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockEvents } from '@/mocks/mockEvents'
import { RoleCode } from '@/types/enums/RoleCode'

export default function StaffDashboardPage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()
    const [manualCode, setManualCode] = useState("")
    const [selectedEvent, setSelectedEvent] = useState("")
    const [scanResult, setScanResult] = useState<{
        success: boolean
        message: string
        ticket?: any
    } | null>(null)
    const [showScanner, setShowScanner] = useState(false)
    const [eventDetailsOpen, setEventDetailsOpen] = useState(false)

    const assignedEvents = mockEvents.slice(0, 3)
    const currentEvent = assignedEvents.find((e) => e.id === selectedEvent) || assignedEvents[0]

    useEffect(() => {
        if (!isLoading && (!user || user.role.code !== RoleCode.STAFF)) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    useEffect(() => {
        if (assignedEvents.length > 0 && !selectedEvent) {
            setSelectedEvent(assignedEvents[0].id)
        }
    }, [assignedEvents, selectedEvent])

    const validateTicket = (code: string) => {
        const isValid = Math.random() > 0.3

        if (isValid) {
            setScanResult({
                success: true,
                message: "Ticket válido",
                ticket: {
                    eventTitle: currentEvent?.title || "Evento",
                    area: "Campo",
                    seat: "Admisión General",
                    holder: "Juan Pérez",
                },
            })
        } else {
            setScanResult({
                success: false,
                message: "Ticket inválido o ya utilizado",
            })
        }

        setTimeout(() => {
            setScanResult(null)
            setManualCode("")
            setShowScanner(false)
        }, 3000)
    }

    const handleManualValidation = (e: React.FormEvent) => {
        e.preventDefault()
        if (manualCode.trim()) {
            validateTicket(manualCode)
        }
    }

    const handleScan = (result: any) => {
        if (result) {
            validateTicket(result.text)
        }
    }

    if (isLoading || !user || user.role.code !== RoleCode.STAFF) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <main className="container mx-auto px-4 py-6 max-w-3xl">
            <h1 className="text-2xl md:text-3xl font-bold mb-6 gradient-text">Validación de Tickets</h1>

            <Collapsible open={eventDetailsOpen} onOpenChange={setEventDetailsOpen} className="mb-6">
                <Card>
                    <CollapsibleTrigger asChild>
                        <CardHeader className="cursor-pointer hover:bg-muted/50 transition-colors">
                            <div className="flex items-center justify-between">
                                <div className="flex-1">
                                    <p className="text-sm text-muted-foreground mb-1">Validando evento:</p>
                                    <CardTitle className="text-xl">{currentEvent?.title}</CardTitle>
                                </div>
                                <ChevronDown className={`h-5 w-5 transition-transform ${eventDetailsOpen ? "rotate-180" : ""}`} />
                            </div>
                        </CardHeader>
                    </CollapsibleTrigger>
                    <CollapsibleContent>
                        <CardContent className="pt-0 space-y-4">
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm pb-4 border-b">
                                <div className="flex items-center gap-2 text-muted-foreground">
                                    <Calendar className="h-4 w-4 shrink-0" />
                                    <span>
                                        {new Date(currentEvent?.date || "").toLocaleDateString("es-ES")} - {currentEvent?.time}
                                    </span>
                                </div>
                                <div className="flex items-center gap-2 text-muted-foreground">
                                    <MapPin className="h-4 w-4 shrink-0" />
                                    <span>{currentEvent?.location}</span>
                                </div>
                                <div className="flex items-center gap-2 text-muted-foreground">
                                    <Users className="h-4 w-4 shrink-0" />
                                    <span>Capacidad: {currentEvent?.areas.reduce((sum, a) => sum + a.capacity, 0)}</span>
                                </div>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="event-select" className="text-sm font-medium">
                                    Cambiar evento
                                </Label>
                                <Select value={selectedEvent} onValueChange={setSelectedEvent}>
                                    <SelectTrigger id="event-select" className="cursor-pointer">
                                        <SelectValue placeholder="Selecciona un evento" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {assignedEvents.map((event) => (
                                            <SelectItem key={event.id} value={event.id} className="cursor-pointer">
                                                <div className="flex flex-col">
                                                    <span className="font-medium">{event.title}</span>
                                                    <span className="text-xs text-muted-foreground">
                                                        {new Date(event.date).toLocaleDateString("es-ES")} - {event.time}
                                                    </span>
                                                </div>
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                        </CardContent>
                    </CollapsibleContent>
                </Card>
            </Collapsible>

            {scanResult && (
                <Card
                    className={`mb-6 border-2 ${scanResult.success
                            ? "border-green-500 bg-green-50 dark:bg-green-950"
                            : "border-red-500 bg-red-50 dark:bg-red-950"
                        }`}
                >
                    <CardContent className="pt-6">
                        <div className="flex items-start gap-4">
                            {scanResult.success ? (
                                <CheckCircle2 className="h-10 w-10 text-green-500 shrink-0" />
                            ) : (
                                <XCircle className="h-10 w-10 text-red-500 shrink-0" />
                            )}
                            <div className="flex-1">
                                <h3
                                    className={`font-bold text-xl mb-2 ${scanResult.success ? "text-green-700 dark:text-green-300" : "text-red-700 dark:text-red-300"
                                        }`}
                                >
                                    {scanResult.message}
                                </h3>
                                {scanResult.ticket && (
                                    <div className="space-y-1 text-sm">
                                        <p>
                                            <span className="font-medium">Evento:</span> {scanResult.ticket.eventTitle}
                                        </p>
                                        <p>
                                            <span className="font-medium">Área:</span> {scanResult.ticket.area}
                                        </p>
                                        <p>
                                            <span className="font-medium">Asiento:</span> {scanResult.ticket.seat}
                                        </p>
                                        <p>
                                            <span className="font-medium">Titular:</span> {scanResult.ticket.holder}
                                        </p>
                                    </div>
                                )}
                            </div>
                        </div>
                    </CardContent>
                </Card>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* QR Scanner */}
                <Card className="h-fit">
                    <CardHeader className="pb-4">
                        <CardTitle className="flex items-center gap-2 text-base md:text-lg">
                            <Camera className="h-5 w-5" />
                            Escanear QR
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        {showScanner ? (
                            <div className="space-y-4">
                                <div className="aspect-square w-full overflow-hidden rounded-lg border-2 border-primary">
                                    {/* <QrReader
                                        constraints={{ facingMode: "environment" }}
                                        onResult={handleScan}
                                        className="w-full h-full"
                                    /> */}
                                </div>
                                <Button
                                    variant="outline"
                                    className="w-full cursor-pointer bg-transparent"
                                    onClick={() => setShowScanner(false)}
                                >
                                    Cancelar
                                </Button>
                            </div>
                        ) : (
                            <Button
                                className="w-full gradient-brand text-white cursor-pointer h-12"
                                size="lg"
                                onClick={() => setShowScanner(true)}
                            >
                                <QrCode className="mr-2 h-5 w-5" />
                                Activar Cámara
                            </Button>
                        )}
                    </CardContent>
                </Card>

                {/* Manual Entry */}
                <Card className="h-fit">
                    <CardHeader className="pb-4">
                        <CardTitle className="flex items-center gap-2 text-base md:text-lg">
                            <Hash className="h-5 w-5" />
                            Código Manual
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleManualValidation} className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="code">Código del Ticket</Label>
                                <Input
                                    id="code"
                                    placeholder="QR-FEST-ROCK-001"
                                    value={manualCode}
                                    onChange={(e) => setManualCode(e.target.value)}
                                    className="h-12"
                                />
                            </div>
                            <Button type="submit" className="w-full cursor-pointer h-12 bg-transparent" variant="outline">
                                Validar Código
                            </Button>
                        </form>
                    </CardContent>
                </Card>
            </div>
        </main>
    )
}
