"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Calendar, MapPin, Users } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { StaffSidebar } from '@/components/StaffSidebar'
import { mockEvents } from '@/mocks/mockEvents'
import { RoleCode } from '@/types/enums/RoleCode'

export default function StaffEventsPage() {
  const router = useRouter()
  const { user, isLoading } = useAuth()

  // Mock assigned events
  const assignedEvents = mockEvents.slice(0, 3)

  useEffect(() => {
    if (!isLoading && (!user || user.role.code !== RoleCode.STAFF)) {
      router.push("/dashboard")
    }
  }, [user, isLoading, router])

  if (isLoading || !user || user.role.code !== RoleCode.STAFF) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <div className="flex min-h-screen">
      <StaffSidebar />

      <div className="flex-1 overflow-auto">
        <main className="container mx-auto px-4 py-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold gradient-text">Mis Eventos Asignados</h1>
            <p className="text-muted-foreground">Eventos donde tienes permisos de validación</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {assignedEvents.map((event) => (
              <Card key={event.id} className="hover:shadow-lg transition-shadow">
                <CardHeader>
                  <div className="aspect-video relative overflow-hidden rounded-lg mb-4">
                    <img
                      src={event.image || "/placeholder.svg"}
                      alt={event.title}
                      className="object-cover w-full h-full"
                    />
                  </div>
                  <CardTitle className="line-clamp-2">{event.title}</CardTitle>
                  <Badge className="gradient-brand text-white border-0 w-fit">{event.category}</Badge>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Calendar className="h-4 w-4 shrink-0" />
                    <span>
                      {new Date(event.date).toLocaleDateString("es-ES")} - {event.time}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <MapPin className="h-4 w-4 shrink-0" />
                    <span className="line-clamp-1">{event.location}</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Users className="h-4 w-4 shrink-0" />
                    <span>Capacidad: {event.availableTickets}</span>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </main>
      </div>
    </div>
  )
}
