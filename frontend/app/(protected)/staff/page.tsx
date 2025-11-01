"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { QrCode, CheckCircle2, XCircle, Search, Calendar, MapPin, Users } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { StaffSidebar } from '@/components/StaffSidebar'
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
    const [stats, setStats] = useState({
        validated: 42,
        pending: 158,
        total: 200,
    })

    const assignedEvents = mockEvents.slice(0, 3)
    const currentEvent = assignedEvents.find((e) => e.id === selectedEvent) || assignedEvents[0]

    useEffect(() => {
        if (!isLoading && (!user || user.role.code !== RoleCode.STAFF)) {
            router.push("/dashboard")
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
            setStats((prev) => ({
                ...prev,
                validated: prev.validated + 1,
                pending: prev.pending - 1,
            }))
        } else {
            setScanResult({
                success: false,
                message: "Ticket inválido o ya utilizado",
            })
        }

        setTimeout(() => {
            setScanResult(null)
            setManualCode("")
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
            setShowScanner(false)
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
        <div className="flex min-h-screen">
            <StaffSidebar />

            <div className="flex-1 overflow-auto">
                <main className="container mx-auto px-4 py-8 max-w-2xl">
                    <h1 className="text-3xl font-bold mb-2 gradient-text">Panel de Staff</h1>
                    <p className="text-muted-foreground mb-8">Validación de tickets por evento</p>

                    <Card className="mb-6">
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Calendar className="h-5 w-5" />
                                Evento Actual
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="space-y-2">
                                <Label htmlFor="event-select">Selecciona el evento a validar</Label>
                                <Select value={selectedEvent} onValueChange={setSelectedEvent}>
                                    <SelectTrigger id="event-select" className="cursor-pointer">
                                        <SelectValue placeholder="Selecciona un evento" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {assignedEvents.map((event) => (
                                            <SelectItem key={event.id} value={event.id} className="cursor-pointer">
                                                {event.title}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            {currentEvent && (
                                <div className="p-4 bg-muted rounded-lg space-y-2">
                                    <h3 className="font-semibold text-lg">{currentEvent.title}</h3>
                                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                        <Calendar className="h-4 w-4" />
                                        <span>
                                            {new Date(currentEvent.date).toLocaleDateString("es-ES")} - {currentEvent.time}
                                        </span>
                                    </div>
                                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                        <MapPin className="h-4 w-4" />
                                        <span>{currentEvent.location}</span>
                                    </div>
                                    <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                        <Users className="h-4 w-4" />
                                        <span>Capacidad: {currentEvent.availableTickets} tickets</span>
                                    </div>
                                </div>
                            )}
                        </CardContent>
                    </Card>

                    {/* Stats */}
                    <div className="grid grid-cols-3 gap-4 mb-8">
                        <Card>
                            <CardContent className="pt-6 text-center">
                                <p className="text-2xl font-bold text-green-500">{stats.validated}</p>
                                <p className="text-xs text-muted-foreground">Validados</p>
                            </CardContent>
                        </Card>
                        <Card>
                            <CardContent className="pt-6 text-center">
                                <p className="text-2xl font-bold text-yellow-500">{stats.pending}</p>
                                <p className="text-xs text-muted-foreground">Pendientes</p>
                            </CardContent>
                        </Card>
                        <Card>
                            <CardContent className="pt-6 text-center">
                                <p className="text-2xl font-bold">{stats.total}</p>
                                <p className="text-xs text-muted-foreground">Total</p>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Scan Result */}
                    {scanResult && (
                        <Card
                            className={`mb-6 ${scanResult.success
                                    ? "border-green-500 bg-green-50 dark:bg-green-950"
                                    : "border-red-500 bg-red-50 dark:bg-red-950"
                                }`}
                        >
                            <CardContent className="pt-6">
                                <div className="flex items-start gap-4">
                                    {scanResult.success ? (
                                        <CheckCircle2 className="h-8 w-8 text-green-500 shrink-0" />
                                    ) : (
                                        <XCircle className="h-8 w-8 text-red-500 shrink-0" />
                                    )}
                                    <div className="flex-1">
                                        <h3
                                            className={`font-bold text-lg mb-2 ${scanResult.success ? "text-green-700 dark:text-green-300" : "text-red-700 dark:text-red-300"
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

                    {/* QR Scanner */}
                    <Card className="mb-6">
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <QrCode className="h-5 w-5" />
                                Escanear QR
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            {showScanner ? (
                                <div className="space-y-4">
                                    <div className="aspect-square w-full max-w-sm mx-auto overflow-hidden rounded-lg">
                                        {/* <QrReader
                                            constraints={{ facingMode: "environment" }}
                                            onResult={handleScan}
                                            className="w-full h-full"
                                        /> */}
                                    </div>
                                    <Button
                                        variant="outline"
                                        className="w-full bg-transparent cursor-pointer"
                                        onClick={() => setShowScanner(false)}
                                    >
                                        Cancelar
                                    </Button>
                                </div>
                            ) : (
                                <Button
                                    className="w-full gradient-brand text-white cursor-pointer"
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
                    <Card>
                        <CardHeader>
                            <CardTitle className="flex items-center gap-2">
                                <Search className="h-5 w-5" />
                                Validación Manual
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
                                    />
                                </div>
                                <Button type="submit" className="w-full bg-transparent cursor-pointer" variant="outline">
                                    Validar Código
                                </Button>
                            </form>
                        </CardContent>
                    </Card>
                </main>
            </div>
        </div>
    )
}
