"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockTickets } from '@/mocks/mockTickets'
import { Navbar } from '@/components/Navbar'
import LanyardTicket from '@/components/tickets/LanyardTicket'
import { usePreloadLanyardAssets } from '@/hooks/usePreloaderLanyardAssets'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'
import TicketCard from '@/components/tickets/TicketCard'
import { Ticket } from '@/types/Ticket'
import { useTicketsByUser } from '@/hooks/ticket/useTicketsByUser'
import { TicketStatus } from '@/types/enums/TicketStatus'

export default function MyTicketsPage() {
    const router = useRouter();
    const { user, isLoading: isLoadingAuth } = useAuth();
    const { data: tickets, isLoading: isLoadingTickets } = useTicketsByUser();
    const [selectedTicket, setSelectedTicket] = useState<Ticket | null>(null);

    usePreloadLanyardAssets();

    useEffect(() => {
        if (!isLoadingAuth && !user) {
            router.push("/login")
        }
    }, [user, isLoadingAuth, router])

    if (isLoadingAuth || !user || isLoadingTickets || !tickets) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    };

    const validTickets = tickets.filter((t: Ticket) => t.status === TicketStatus.ISSUED);
    const usedTickets = tickets.filter((t: Ticket) => t.status === TicketStatus.CHECKED_IN);

    console.log("valid tickets", validTickets);
    const handleViewQR = (ticket: Ticket) => {
        setSelectedTicket(ticket)
    }

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-8">

                <GradientText>
                    <h1 className="text-3xl font-bold mb-8">Mis Tickets</h1>
                </GradientText>

                <Tabs defaultValue="valid" className="space-y-6">
                    <TabsList>
                        <TabsTrigger value="valid">Activos ({validTickets.length})</TabsTrigger>
                        <TabsTrigger value="used">Usados ({usedTickets.length})</TabsTrigger>
                    </TabsList>

                    <TabsContent value="valid" className="space-y-4">
                        {validTickets.length > 0 ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {validTickets.map((ticket: Ticket) => (
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
                                {usedTickets.map((ticket: Ticket) => (
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
                        code={selectedTicket.code}
                        qrCode={selectedTicket.qrBase64}
                        eventTitle={selectedTicket.event?.eventTitle}
                        areaName={selectedTicket.event?.area?.name}
                        seatNumber={selectedTicket.event?.area?.seat || null}
                        onClose={() => setSelectedTicket(null)}
                    />
                )}

            </main>
        </div>
    )
}

//TODO: Usar type Ticket
