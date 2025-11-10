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
import LanyardTicket from '@/components/tickets/LanyardTicket'
import { usePreloadLanyardAssets } from '@/hooks/usePreloaderLanyardAssets'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'
import TicketCard from '@/components/tickets/TicketCard'

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
