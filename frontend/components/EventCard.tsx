import Link from "next/link"
import Image from "next/image"
import { Calendar, MapPin, Ticket } from "lucide-react"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"

//TODO: Borrar
export const mockEvents: Event[] = [
  {
    id: "1",
    title: "Festival de Música Urbana 2025",
    description:
      "Vive una noche inolvidable con los mejores artistas del trap y reggaetón. Luces, sonido y energía sin igual.",
    category: "Concierto",
    date: "2025-11-15T20:00:00",
    time: "20:00",
    location: "Estadio Monumental, Buenos Aires",
    availableTickets: 320,
    price: 15000,
    image:
      "https://images.unsplash.com/photo-1507874457470-272b3c8d8ee2?q=80&w=1600&auto=format&fit=crop",
  },
  {
    id: "2",
    title: "Expo Gamer Argentina",
    description:
      "La convención gamer más grande del país. Stands, torneos, cosplay y lanzamientos exclusivos.",
    category: "Convención",
    date: "2025-12-02T10:00:00",
    time: "10:00",
    location: "Centro Costa Salguero, Buenos Aires",
    availableTickets: 850,
    price: 8000,
    image:
      "https://images.unsplash.com/photo-1616469829935-c2f9431c8d8a?q=80&w=1600&auto=format&fit=crop",
  },
  {
    id: "3",
    title: "Stand-Up Night con Pablo Mira",
    description:
      "Una noche de humor ácido, actualidad y risas con uno de los comediantes más queridos del país.",
    category: "Comedia",
    date: "2025-11-20T21:30:00",
    time: "21:30",
    location: "Teatro Broadway, Rosario",
    availableTickets: 210,
    price: 11000,
    image:
      "https://images.unsplash.com/photo-1599407380683-1f7f8bb29b88?q=80&w=1600&auto=format&fit=crop",
  },
  {
    id: "4",
    title: "Feria del Libro Independiente",
    description:
      "Encuentro de editoriales, escritores y lectores. Talleres, charlas y presentaciones durante toda la jornada.",
    category: "Cultural",
    date: "2025-11-10T09:00:00",
    time: "09:00",
    location: "La Rural, Buenos Aires",
    availableTickets: 1200,
    price: 3000,
    image:
      "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?q=80&w=1600&auto=format&fit=crop",
  },
  {
    id: "5",
    title: "Electro Beach Festival",
    description:
      "Tres días de música electrónica frente al mar con DJs internacionales y una experiencia única.",
    category: "Festival",
    date: "2025-12-20T18:00:00",
    time: "18:00",
    location: "Playa Grande, Mar del Plata",
    availableTickets: 540,
    price: 18000,
    image:
      "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?q=80&w=1600&auto=format&fit=crop",
  },
]

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
