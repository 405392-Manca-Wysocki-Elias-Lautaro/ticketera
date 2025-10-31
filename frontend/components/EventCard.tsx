import Link from "next/link"
import Image from "next/image"
import { Calendar, MapPin, Ticket } from "lucide-react"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"

//TODO: Borrar
export interface Event {
  id: string
  title: string
  description: string
  category: string
  date: string
  time: string
  location: string
  availableTickets: number
  price: number
  image?: string
}



interface EventCardProps {
  event: Event
}

export function EventCard({ event }: EventCardProps) {
  const formattedDate = new Date(event.date).toLocaleDateString("es-ES", {
    day: "numeric",
    month: "long",
    year: "numeric",
  })

  return (
    <Card className="overflow-hidden hover:shadow-lg transition-shadow">
      <Link href={`/event/${event.id}`}>
        <div className="relative h-48 w-full">
          <Image src={event.image || "/placeholder.svg"} alt={event.title} fill className="object-cover" />
          <Badge className="absolute top-3 right-3 gradient-brand text-white border-0">{event.category}</Badge>
        </div>
      </Link>

      <CardContent className="p-4 space-y-3">
        <Link href={`/event/${event.id}`}>
          <h3 className="font-bold text-lg line-clamp-1 hover:text-primary transition-colors">{event.title}</h3>
        </Link>

        <p className="text-sm text-muted-foreground line-clamp-2">{event.description}</p>

        <div className="space-y-2 text-sm">
          <div className="flex items-center gap-2 text-muted-foreground">
            <Calendar className="h-4 w-4 shrink-0" />
            <span className="line-clamp-1">
              {formattedDate} - {event.time}
            </span>
          </div>
          <div className="flex items-center gap-2 text-muted-foreground">
            <MapPin className="h-4 w-4 shrink-0" />
            <span className="line-clamp-1">{event.location}</span>
          </div>
          <div className="flex items-center gap-2 text-muted-foreground">
            <Ticket className="h-4 w-4 shrink-0" />
            <span>{event.availableTickets} entradas disponibles</span>
          </div>
        </div>
      </CardContent>

      <CardFooter className="p-4 pt-0 flex items-center justify-between">
        <div>
          <p className="text-xs text-muted-foreground">Desde</p>
          <p className="text-xl font-bold gradient-text">${event.price.toLocaleString()}</p>
        </div>
        <Button asChild className="gradient-brand text-white">
          <Link href={`/event/${event.id}`}>Ver Detalles</Link>
        </Button>
      </CardFooter>
    </Card>
  )
}
