"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Navbar } from "@/components/Navbar"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Calendar, MapPin, QrCode } from "lucide-react"
import { QRCodeSVG } from "qrcode.react"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockTickets } from '@/mocks/mockTickets'

export default function MyTicketsPage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()
    //TODO: Usar type TICKETS
    const [tickets, setTickets] = useState<any[]>(mockTickets)
    const [selectedTicket, setSelectedTicket] = useState<any | null>(null)

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [isLoading, router])

    if (isLoading || !user) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    const validTickets = tickets.filter((t) => t.status === "valid")
    const usedTickets = tickets.filter((t) => t.status === "used")

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-8">
                <h1 className="text-3xl font-bold mb-8 gradient-text">Mis Tickets</h1>

                <Tabs defaultValue="valid" className="space-y-6">
                    <TabsList>
                        <TabsTrigger value="valid">Activos ({validTickets.length})</TabsTrigger>
                        <TabsTrigger value="used">Usados ({usedTickets.length})</TabsTrigger>
                    </TabsList>

                    <TabsContent value="valid" className="space-y-4">
                        {validTickets.length > 0 ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {validTickets.map((ticket) => (
                                    <TicketCard key={ticket.id} ticket={ticket} onViewQR={() => setSelectedTicket(ticket)} />
                                ))}
                            </div>
                        ) : (
                            <Card>
                                <CardContent className="py-12 text-center">
                                    <p className="text-muted-foreground">No tienes tickets activos</p>
                                    <Button asChild className="mt-4 gradient-brand text-white">
                                        <a href="/dashboard">Explorar Eventos</a>
                                    </Button>
                                </CardContent>
                            </Card>
                        )}
                    </TabsContent>

                    <TabsContent value="used" className="space-y-4">
                        {usedTickets.length > 0 ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {usedTickets.map((ticket) => (
                                    <TicketCard key={ticket.id} ticket={ticket} />
                                ))}
                            </div>
                        ) : (
                            <Card>
                                <CardContent className="py-12 text-center">
                                    <p className="text-muted-foreground">No tienes tickets usados</p>
                                </CardContent>
                            </Card>
                        )}
                    </TabsContent>
                </Tabs>

                {/* QR Modal */}
                {selectedTicket && (
                    <div
                        className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50"
                        onClick={() => setSelectedTicket(null)}
                    >
                        <Card className="max-w-md w-full" onClick={(e) => e.stopPropagation()}>
                            <CardContent className="p-6 space-y-4">
                                <div className="text-center">
                                    <h3 className="font-bold text-lg mb-2">{selectedTicket.eventTitle}</h3>
                                    <p className="text-sm text-muted-foreground">
                                        {selectedTicket.areaName}
                                        {selectedTicket.seatNumber && ` - Asiento ${selectedTicket.seatNumber}`}
                                    </p>
                                </div>

                                <div className="flex justify-center bg-white p-6 rounded-lg">
                                    <QRCodeSVG value={selectedTicket.qrCode} size={200} />
                                </div>

                                <div className="text-center">
                                    <p className="text-xs text-muted-foreground mb-1">Código</p>
                                    <p className="font-mono font-semibold">{selectedTicket.qrCode}</p>
                                </div>

                                <Button variant="outline" className="w-full bg-transparent" onClick={() => setSelectedTicket(null)}>
                                    Cerrar
                                </Button>
                            </CardContent>
                        </Card>
                    </div>
                )}
            </main>
        </div>
    )
}

//TODO: Añadir type Ticket y separar en otra clase (Ver tema componentes anidados)
function TicketCard({ ticket, onViewQR }: { ticket: any; onViewQR?: () => void }) {
    const formattedDate = new Date(ticket.eventDate).toLocaleDateString("es-ES", {
        day: "numeric",
        month: "long",
        year: "numeric",
    })

    return (
        <Card className={ticket.status === "used" ? "opacity-60" : ""}>
            <CardContent className="p-6 space-y-4">
                <div className="flex items-start justify-between">
                    <div className="flex-1">
                        <h3 className="font-bold text-lg line-clamp-2 mb-1">{ticket.eventTitle}</h3>
                        <Badge variant={ticket.status === "valid" ? "default" : "secondary"}>
                            {ticket.status === "valid" ? "Válido" : "Usado"}
                        </Badge>
                    </div>
                </div>

                <div className="space-y-2 text-sm">
                    <div className="flex items-center gap-2 text-muted-foreground">
                        <Calendar className="h-4 w-4 shrink-0" />
                        <span>
                            {formattedDate} - {ticket.eventTime}
                        </span>
                    </div>
                    <div className="flex items-center gap-2 text-muted-foreground">
                        <MapPin className="h-4 w-4 shrink-0" />
                        <span>{ticket.eventLocation}</span>
                    </div>
                </div>

                <div className="pt-4 border-t space-y-2">
                    <div className="flex justify-between text-sm">
                        <span className="text-muted-foreground">Área</span>
                        <span className="font-medium">{ticket.areaName}</span>
                    </div>
                    {ticket.seatNumber && (
                        <div className="flex justify-between text-sm">
                            <span className="text-muted-foreground">Asiento</span>
                            <span className="font-medium">{ticket.seatNumber}</span>
                        </div>
                    )}
                    <div className="flex justify-between text-sm">
                        <span className="text-muted-foreground">Precio</span>
                        <span className="font-medium">${ticket.price.toLocaleString()}</span>
                    </div>
                </div>

                {ticket.status === "valid" && onViewQR && (
                    <Button onClick={onViewQR} className="w-full gradient-brand text-white">
                        <QrCode className="mr-2 h-4 w-4" />
                        Ver Código QR
                    </Button>
                )}
            </CardContent>
        </Card>
    )
}
