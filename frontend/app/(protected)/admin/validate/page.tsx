"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import { Camera, Hash, CheckCircle2, XCircle, ChevronDown, Calendar, MapPin, Users } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockEvents } from '@/mocks/mockEvents'
import { RoleCode } from '@/types/enums/RoleCode'
import GradientText from '@/components/GradientText'
import { RoleUtils } from '@/utils/roleUtils'

export default function AdminValidatePage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()
    const [selectedEventId, setSelectedEventId] = useState<string>("")
    const [ticketCode, setTicketCode] = useState("")
    const [validationResult, setValidationResult] = useState<"valid" | "invalid" | null>(null)
    const [isEventDetailsOpen, setIsEventDetailsOpen] = useState(false)

    // Mock: Admin can validate tickets for all events
    const assignedEvents = mockEvents.slice(0, 5)

    useEffect(() => {
        if (!isLoading && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoading, router])

    const selectedEvent = assignedEvents.find((e) => e.id === selectedEventId)

    const handleValidateTicket = () => {
        // Mock validation
        const isValid = Math.random() > 0.3
        setValidationResult(isValid ? "valid" : "invalid")
        setTimeout(() => {
            setValidationResult(null)
            setTicketCode("")
        }, 3000)
    }

    if (isLoading || !user || !RoleUtils.isAdmin(user)) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <main className="container mx-auto px-4 py-8 max-w-4xl">
            <div className="mb-8">
                <GradientText>
                    <h1 className="text-3xl font-bold mb-2">Validación de Tickets</h1>
                </GradientText>
                <p className="text-muted-foreground">
                    Valida tickets escaneando el código QR o ingresando el código manualmente
                </p>
            </div>

            <div className="space-y-6">
                {/* Event Selector */}
                <Card>
                    <CardHeader>
                        <Label htmlFor="event-select" className="text-base font-semibold">
                            Seleccionar Evento
                        </Label>
                    </CardHeader>
                    <CardContent>
                        <Select value={selectedEventId} onValueChange={setSelectedEventId}>
                            <SelectTrigger id="event-select" className="cursor-pointer">
                                <SelectValue placeholder="Selecciona un evento para validar" />
                            </SelectTrigger>
                            <SelectContent>
                                {assignedEvents.map((event) => (
                                    <SelectItem key={event.id} value={event.id} className="cursor-pointer">
                                        {event.title} - {new Date(event.date).toLocaleDateString("es-ES")}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </CardContent>
                </Card>

                {/* Event Details Collapsible */}
                {selectedEvent && (
                    <Collapsible open={isEventDetailsOpen} onOpenChange={setIsEventDetailsOpen}>
                        <Card>
                            <CollapsibleTrigger className="w-full cursor-pointer">
                                <CardHeader className="flex flex-row items-center justify-between">
                                    <div className="text-left">
                                        <GradientText>
                                            <CardTitle>{selectedEvent.title}</CardTitle>
                                        </GradientText>
                                        <p className="text-sm text-muted-foreground mt-1">Haz clic para ver detalles del evento</p>
                                    </div>
                                    <ChevronDown className={`h-5 w-5 transition-transform ${isEventDetailsOpen ? "rotate-180" : ""}`} />
                                </CardHeader>
                            </CollapsibleTrigger>
                            <CollapsibleContent>
                                <CardContent className="space-y-4 pt-0">
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div className="flex items-start gap-3">
                                            <Calendar className="h-5 w-5 text-primary mt-0.5" />
                                            <div>
                                                <p className="font-medium">Fecha y Hora</p>
                                                <p className="text-sm text-muted-foreground">
                                                    {new Date(selectedEvent.date).toLocaleDateString("es-ES", {
                                                        weekday: "long",
                                                        year: "numeric",
                                                        month: "long",
                                                        day: "numeric",
                                                    })}
                                                </p>
                                                <p className="text-sm text-muted-foreground">{selectedEvent.time}</p>
                                            </div>
                                        </div>
                                        <div className="flex items-start gap-3">
                                            <MapPin className="h-5 w-5 text-primary mt-0.5" />
                                            <div>
                                                <p className="font-medium">Ubicación</p>
                                                <p className="text-sm text-muted-foreground">{selectedEvent.location}</p>
                                            </div>
                                        </div>
                                        <div className="flex items-start gap-3">
                                            <Users className="h-5 w-5 text-primary mt-0.5" />
                                            <div>
                                                <p className="font-medium">Capacidad</p>
                                                <p className="text-sm text-muted-foreground">
                                                    {selectedEvent.availableTickets} tickets disponibles
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                    <div>
                                        <p className="font-medium mb-2">Áreas del Evento</p>
                                        <div className="flex flex-wrap gap-2">
                                            {selectedEvent.areas.map((area) => (
                                                <Badge key={area.id} variant="outline">
                                                    {area.name} - ${area.price.toLocaleString()}
                                                </Badge>
                                            ))}
                                        </div>
                                    </div>
                                </CardContent>
                            </CollapsibleContent>
                        </Card>
                    </Collapsible>
                )}

                {/* Validation Interface */}
                {selectedEvent && (
                    <Card>
                        <CardContent className="pt-6">
                            <Tabs defaultValue="camera" className="w-full">
                                <TabsList className="grid w-full grid-cols-2">
                                    <TabsTrigger value="camera" className="cursor-pointer">
                                        <Camera className="mr-2 h-4 w-4" />
                                        Escanear QR
                                    </TabsTrigger>
                                    <TabsTrigger value="manual" className="cursor-pointer">
                                        <Hash className="mr-2 h-4 w-4" />
                                        Código Manual
                                    </TabsTrigger>
                                </TabsList>

                                <TabsContent value="camera" className="space-y-4">
                                    <div className="aspect-square max-w-md mx-auto bg-muted rounded-lg flex items-center justify-center">
                                        <div className="text-center space-y-2">
                                            <Camera className="h-12 w-12 mx-auto text-muted-foreground" />
                                            <p className="text-sm text-muted-foreground">Cámara QR (Demo)</p>
                                            <p className="text-xs text-muted-foreground">En producción, aquí se activaría la cámara</p>
                                        </div>
                                    </div>
                                </TabsContent>

                                <TabsContent value="manual" className="space-y-4">
                                    <div className="space-y-2">
                                        <Label htmlFor="ticket-code">Código del Ticket</Label>
                                        <Input
                                            id="ticket-code"
                                            placeholder="Ingresa el código del ticket"
                                            value={ticketCode}
                                            onChange={(e) => setTicketCode(e.target.value)}
                                        />
                                    </div>
                                    <Button
                                        onClick={handleValidateTicket}
                                        disabled={!ticketCode}
                                        className="w-full gradient-brand text-white cursor-pointer"
                                    >
                                        Validar Ticket
                                    </Button>
                                </TabsContent>
                            </Tabs>

                            {/* Validation Result */}
                            {validationResult && (
                                <div className="mt-6">
                                    {validationResult === "valid" ? (
                                        <div className="bg-green-50 dark:bg-green-950 border border-green-200 dark:border-green-800 rounded-lg p-4 flex items-center gap-3">
                                            <CheckCircle2 className="h-8 w-8 text-green-600 dark:text-green-400" />
                                            <div>
                                                <p className="font-semibold text-green-900 dark:text-green-100">Ticket Válido</p>
                                                <p className="text-sm text-green-700 dark:text-green-300">
                                                    El ticket ha sido validado correctamente
                                                </p>
                                            </div>
                                        </div>
                                    ) : (
                                        <div className="bg-red-50 dark:bg-red-950 border border-red-200 dark:border-red-800 rounded-lg p-4 flex items-center gap-3">
                                            <XCircle className="h-8 w-8 text-red-600 dark:text-red-400" />
                                            <div>
                                                <p className="font-semibold text-red-900 dark:text-red-100">Ticket Inválido</p>
                                                <p className="text-sm text-red-700 dark:text-red-300">
                                                    El ticket no es válido o ya fue utilizado
                                                </p>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )}
                        </CardContent>
                    </Card>
                )}
            </div>
        </main>
    )
}
