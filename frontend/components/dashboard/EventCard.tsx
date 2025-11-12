import Link from "next/link"
import { Calendar, MapPin, Ticket } from "lucide-react"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import TiltedCard from "@/components/TiltedCard"
import StarBorder from '../StarBorder'
import { Event } from '@/types/Event'
import GradientText from '../GradientText'

interface EventCardProps {
    event: Event
}


export function EventCard({ event }: EventCardProps) {
    const formattedDate = new Date(event.startsAt).toLocaleDateString("es-ES", {
        day: "numeric",
        month: "long",
        // year: "numeric",
    })

    const formattedHour = new Date(event.startsAt).toLocaleTimeString("es-ES", {
        hour: "2-digit",
        minute: "2-digit",
    })

    return (
        <Card className="overflow-hidden hover:shadow-lg transition-shadow pt-0">
            <Link href={`/event/${event.id}`}>
                <div className="relative h-48 w-full">
                    <TiltedCard
                        imageSrc={event.coverUrl || "/placeholder.svg"}
                        altText={event.title}
                        captionText={event.title}
                        containerHeight="12rem"
                        imageHeight="12rem"
                        imageWidth="100%"
                        scaleOnHover={1.05}
                        rotateAmplitude={12}
                        showMobileWarning={false}
                        showTooltip={false}
                        displayOverlayContent
                        overlayContent={
                            <Badge className="absolute top-5 right-8 gradient-brand  text-white border-0">
                                {event?.categoryName}
                            </Badge>
                        }
                    />
                </div>
            </Link>

            <CardContent className="p-4 space-y-3">
                <Link href={`/event/${event.id}`}>
                    <h3 className="font-bold text-lg line-clamp-1 hover:text-primary transition-colors">
                        {event.title}
                    </h3>
                </Link>

                <p className="text-sm text-muted-foreground line-clamp-2">{event.description}</p>

                <div className="space-y-2 text-sm">
                    <div className="flex items-center gap-2 text-muted-foreground">
                        <Calendar className="h-4 w-4 shrink-0" />
                        <span className="line-clamp-1">
                            {formattedDate} - {formattedHour}
                        </span>
                    </div>
                    <div className="flex items-center gap-2 text-muted-foreground">
                        <MapPin className="h-4 w-4 shrink-0" />
                        <span className="line-clamp-1">{event.venueName}</span>
                    </div>
                    <div className="flex items-center gap-2 text-muted-foreground">
                        <Ticket className="h-4 w-4 shrink-0" />
                        <span>{event.totalAvailableTickets} entradas disponibles</span>
                    </div>
                </div>
            </CardContent>

            <CardFooter className="p-4 pt-0 flex items-center justify-between">

                <div>
                    <p className="text-xs text-muted-foreground">Desde</p>
                    <GradientText>
                        <p className="text-xl font-bold">{event.currency}${event.minPriceCents}</p>
                    </GradientText>
                </div>

                <StarBorder>
                    <Button asChild className="gradient-brand text-white">
                        <Link href={`/event/${event.id}`}>Ver Detalles</Link>
                    </Button>
                </StarBorder>

            </CardFooter>
        </Card>
    )
}
