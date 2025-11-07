"use client"

import { useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { useAuth } from "@/hooks/auth/useAuth"
import { Navbar } from "@/components/Navbar"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Calendar, MapPin, Clock, Navigation } from "lucide-react"
import { mockEvents } from '@/mocks/mockEvents'
import TiltedCard from '@/components/TiltedCard'
import GradientText from '@/components/GradientText'
import StarBorder from '@/components/StarBorder'

export default function EventDetailPage() {
    const router = useRouter()
    const params = useParams()
    const { user, isLoading } = useAuth()
    const [event, setEvent] = useState(mockEvents.find((e) => e.id === params.id))

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    if (!event) {
        return (
            <div className="min-h-screen bg-background">
                <Navbar />
                <div className="container mx-auto px-4 py-8">
                    <p className="text-center text-muted-foreground">Evento no encontrado</p>
                </div>
            </div>
        )
    }

    const formattedDate = new Date(event.date).toLocaleDateString("es-ES", {
        weekday: "long",
        day: "numeric",
        month: "long",
        year: "numeric",
    })

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Main Content */}
                    <div className="lg:col-span-2 space-y-6">
                        {/* Event Image */}
                        <div className="relative h-64 md:h-96 w-full rounded-xl overflow-hidden">
                            <TiltedCard
                                imageSrc={event.image || "/placeholder.svg"}
                                altText={event.title}
                                captionText={event.title}
                                containerHeight="100%"
                                imageHeight="100%"
                                imageWidth="100%"
                                scaleOnHover={1.02}
                                rotateAmplitude={6}
                                showMobileWarning={false}
                                showTooltip={false}
                                displayOverlayContent
                                overlayContent={
                                    <Badge className="absolute top-10 right-16 gradient-brand  text-white border-0">
                                        {event.category}
                                    </Badge>
                                }
                            />
                        </div>

                        {/* Event Info */}
                        <div>
                            <h1 className="text-3xl md:text-4xl font-bold mb-4 text-balance">{event.title}</h1>

                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
                                <div className="flex items-start gap-3">
                                    <Calendar className="h-5 w-5 text-primary mt-0.5 shrink-0" />
                                    <div>
                                        <p className="font-medium">Fecha</p>
                                        <p className="text-sm text-muted-foreground capitalize">{formattedDate}</p>
                                    </div>
                                </div>

                                <div className="flex items-start gap-3">
                                    <Clock className="h-5 w-5 text-primary mt-0.5 shrink-0" />
                                    <div>
                                        <p className="font-medium">Hora</p>
                                        <p className="text-sm text-muted-foreground">{event.time}</p>
                                    </div>
                                </div>

                                <div className="flex items-start gap-3">
                                    <MapPin className="h-5 w-5 text-primary mt-0.5 shrink-0" />
                                    <div>
                                        <p className="font-medium">Lugar</p>
                                        <p className="text-sm text-muted-foreground">{event.location}</p>
                                    </div>
                                </div>

                                <div className="flex items-start gap-3">
                                    <Navigation className="h-5 w-5 text-primary mt-0.5 shrink-0" />
                                    <div>
                                        <p className="font-medium">Dirección</p>
                                        <p className="text-sm text-muted-foreground">{event.address}</p>
                                    </div>
                                </div>
                            </div>

                            <div>
                                <h2 className="text-xl font-bold mb-3">Descripción</h2>
                                <p className="text-muted-foreground leading-relaxed text-pretty">{event.description}</p>
                            </div>
                        </div>

                        {/* Areas */}
                        <div>
                            <h2 className="text-xl font-bold mb-4">Áreas Disponibles</h2>
                            <div className="space-y-3">
                                {event.areas.map((area) => (
                                    <Card key={area.id}>
                                        <CardContent className="p-4">
                                            <div className="flex items-center justify-between">
                                                <div>
                                                    <h3 className="font-semibold">{area.name}</h3>
                                                    <p className="text-sm text-muted-foreground">
                                                        {area.type === "general" ? "Admisión General" : "Asientos Numerados"}
                                                    </p>
                                                </div>
                                                <div className="text-right">
                                                    <GradientText>
                                                        <p className="text-lg font-bold">${area.price.toLocaleString()}</p>
                                                    </GradientText>
                                                    <p className="text-xs text-muted-foreground">{area.capacity} lugares</p>
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Sidebar - Purchase */}
                    <div className="lg:col-span-1">
                        <Card className="sticky top-20">
                            <CardContent className="p-6 space-y-4">
                                <div>
                                    <p className="text-sm text-muted-foreground mb-1">Precio desde</p>
                                    <GradientText>
                                        <p className="text-3xl font-bold">${event.price.toLocaleString()}</p>
                                    </GradientText>
                                </div>

                                <div className="space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Entradas disponibles</span>
                                        <span className="font-medium">{event.availableTickets}</span>
                                    </div>
                                </div>

                                <StarBorder className='w-full'>
                                    <Button
                                        className="w-full gradient-brand text-white"
                                        size="lg"
                                        onClick={() => router.push(`/event/${event.id}/select-seats`)}
                                    >
                                        Comprar Entradas
                                    </Button>
                                </StarBorder>

                                <p className="text-xs text-center text-muted-foreground">
                                    Selecciona tus asientos y completa tu compra de forma segura
                                </p>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </main>
        </div>
    )
}
