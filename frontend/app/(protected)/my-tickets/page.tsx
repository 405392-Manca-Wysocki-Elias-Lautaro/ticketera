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
import { Navbar } from '@/components/Navbar'
import LanyardTicket from '@/components/tickets/LanyardTicket'
import { usePreloadLanyardAssets } from '@/hooks/usePreloaderLanyardAssets'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'
import TicketCard from '@/components/tickets/TicketCard'
import { ticketService } from '@/services/ticketService'
import { eventService } from '@/services/eventService'
import { TicketResponse, TicketStatus } from '@/types/Ticket'

export default function MyTicketsPage() {
    const router = useRouter()
    const { user, isLoading: isAuthLoading } = useAuth()
    const [tickets, setTickets] = useState<TicketResponse[]>([])
    const [selectedTicket, setSelectedTicket] = useState<TicketResponse | null>(null)
    const [isLoadingTickets, setIsLoadingTickets] = useState(true)
    const [error, setError] = useState<string | null>(null)

    usePreloadLanyardAssets();

    // Cargar tickets del usuario y enriquecerlos con info del evento
    useEffect(() => {
        async function fetchTickets() {
            if (!user?.id) return;

            try {
                setIsLoadingTickets(true);
                setError(null);
                const userTickets = await ticketService.getUserTickets(user.id);
                
                // Obtener IDs únicos de eventos
                const eventIds = [...new Set(userTickets.map(t => t.occurrenceId))];
                
                // Consultar información de todos los eventos en paralelo
                const eventsMap = await eventService.getEventsByIds(eventIds);
                
                // Enriquecer tickets con información del evento
                const enrichedTickets = userTickets.map(ticket => {
                    const event = eventsMap.get(ticket.occurrenceId);
                    return {
                        ...ticket,
                        eventTitle: event?.title || 'Evento',
                        eventLocation: event?.venueName || event?.city || 'Ubicación por confirmar',
                        eventDate: event?.startsAt
                    };
                });
                
                setTickets(enrichedTickets);
            } catch (error) {
                console.error('Error al cargar tickets:', error);
                setError('No se pudieron cargar tus tickets. Intenta nuevamente.');
            } finally {
                setIsLoadingTickets(false);
            }
        }

        if (!isAuthLoading && user) {
            fetchTickets();
        }
    }, [user, isAuthLoading]);

    // Redirigir si no está autenticado
    useEffect(() => {
        if (!isAuthLoading && !user) {
            router.push("/login")
        }
    }, [user, isAuthLoading, router])

    if (isAuthLoading || !user) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    // Filtrar tickets por estado
    const validTickets = tickets.filter((t) => 
        t.status === TicketStatus.ISSUED
    );
    const usedTickets = tickets.filter((t) => 
        t.status === TicketStatus.CHECKED_IN
    );

    const handleViewQR = (ticket: TicketResponse) => {
        setSelectedTicket(ticket)
    }

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-8">

                <GradientText>
                    <h1 className="text-3xl font-bold mb-8">Mis Tickets</h1>
                </GradientText>

                {error && (
                    <Card className="mb-6 border-destructive">
                        <CardContent className="py-4">
                            <p className="text-destructive">{error}</p>
                        </CardContent>
                    </Card>
                )}

                {isLoadingTickets ? (
                    <div className="flex justify-center py-12">
                        <Loader2 className="h-8 w-8 animate-spin text-primary" />
                    </div>
                ) : (
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
                                        <StarBorder>
                                            <Button asChild className="mt-4 gradient-brand text-white">
                                                <a href="/dashboard">Explorar Eventos</a>
                                            </Button>
                                        </StarBorder>
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
                )}

                {/* QR Modal */}
                {selectedTicket && (
                    <LanyardTicket
                        qrCode={selectedTicket.code}
                        eventTitle="Evento"
                        areaName="Área general"
                        seatNumber={undefined}
                        onClose={() => setSelectedTicket(null)}
                    />
                )}

            </main>
        </div>
    )
}
