"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Calendar, Loader2, MapPin, QrCode } from "lucide-react"
import { QRCodeSVG } from "qrcode.react"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockTickets } from '@/mocks/mockTickets'
import { Navbar } from '@/components/Navbar'
import LanyardTicket from '@/components/LanyardTicket'
import { usePreloadLanyardAssets } from '@/hooks/usePreloaderLanyardAssets'

export default function MyTicketsPage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()
    //TODO: Usar type Ticket
    const [tickets, setTickets] = useState<any[]>(mockTickets)
    //TODO: Usar type Ticket
    const [selectedTicket, setSelectedTicket] = useState<any | null>(null)

    usePreloadLanyardAssets();

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    if (isLoading || !user) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    const validTickets = tickets.filter((t) => t.status === "valid")
    const usedTickets = tickets.filter((t) => t.status === "used")

    const handleViewQR = (ticket: any) => {
        setSelectedTicket(ticket)
    }

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
                                    <TicketCard key={ticket.id} ticket={ticket} onViewQR={() => handleViewQR(ticket)} />
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
                    <LanyardTicket
                        qrCode={selectedTicket.qrCode}
                        eventTitle={selectedTicket.eventTitle}
                        areaName={selectedTicket.areaName}
                        seatNumber={selectedTicket.seatNumber}
                        onClose={() => setSelectedTicket(null)}
                    />
                )}

            </main>
        </div>
    )
}

//TODO: Usar type Ticket
function TicketCard({ ticket, onViewQR }: { ticket: any; onViewQR?: () => void }) {
    const formattedDate = new Date(ticket.eventDate).toLocaleDateString("es-ES", {
        day: "numeric",
        month: "long",
        year: "numeric",
    })

    const [loading, setLoading] = useState(false);

    const handleClick = async ({ onClick }) => {
        setLoading(true);
        await new Promise((r) => setTimeout(r, 200));
        onClick();
        setTimeout(() => setLoading(false), 500);
    };


    return (
        <Card className={ticket.status === "used" ? "opacity-60" : ""}>
            <CardContent className="p-6 flex flex-col h-full">
                <div className="flex-1 space-y-4">
                    <div className="flex items-start justify-between">
                        <div className="flex-1">
                            <h3 className="font-bold text-lg line-clamp-2 mb-1">{ticket.eventTitle}</h3>
                            <Badge variant={ticket.status === "valid" ? "secondary" : "default"}>
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
                </div>

                {ticket.status === "valid" && onViewQR && (
                    <Button
                        onClick={() => handleClick({ onClick: onViewQR })}
                        className="w-full gradient-brand text-white mt-4"
                        disabled={loading}
                    >
                        {loading ? (
                            <>
                                <Loader2 className="animate-spin w-4 h-4" />
                            </>
                        ) : (
                            <>
                                <QrCode className="mr-2 h-4 w-4" />
                                Ver Código QR
                            </>
                        )}
                    </Button>
                )}
            </CardContent>
        </Card>
    )
}
