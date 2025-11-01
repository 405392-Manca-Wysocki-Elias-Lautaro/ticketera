"use client"

import { useEffect, useState, useMemo } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { Calendar, MapPin, Ticket, Plus, Edit, Trash2 } from "lucide-react"
import { useAuth } from '@/hooks/auth/useAuth'
import { AdminSidebar } from '@/components/AdminSidebar'
import { mockEvents } from '@/mocks/mockEvents'
import { RoleCode } from '@/types/enums/RoleCode'

export default function AdminEventsPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { user, isLoading } = useAuth()
  const [events, setEvents] = useState(mockEvents)
  const [deleteEventId, setDeleteEventId] = useState<string | null>(null)

  const searchQuery = useMemo(() => searchParams.get("search")?.toLowerCase() || "", [searchParams])

  const filteredEvents = useMemo(() => {
    if (!searchQuery) return events
    return events.filter(
      (event) =>
        event.title.toLowerCase().includes(searchQuery) ||
        event.category.toLowerCase().includes(searchQuery) ||
        event.location.toLowerCase().includes(searchQuery),
    )
  }, [events, searchQuery])

  useEffect(() => {
    if (!isLoading && (!user || user.role.code !== RoleCode.ADMIN)) {
      router.push("/dashboard")
    }
  }, [user, isLoading, router])

  const handleDeleteEvent = (eventId: string) => {
    setEvents((prev) => prev.filter((e) => e.id !== eventId))
    setDeleteEventId(null)
  }

  if (isLoading || !user || user.role.code !== RoleCode.ADMIN) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <TooltipProvider>
      <div className="flex min-h-screen">
        <AdminSidebar />

        <div className="flex-1 overflow-auto">
          <main className="container mx-auto px-4 py-8">
            <div className="flex items-center justify-between mb-8">
              <div>
                <h1 className="text-3xl font-bold gradient-text">Mis Eventos</h1>
                <p className="text-muted-foreground">
                  {searchQuery ? `Resultados para "${searchQuery}"` : "Gestiona todos tus eventos"}
                </p>
              </div>
              <Button asChild className="gradient-brand text-white cursor-pointer">
                <Link href="/admin/events/create">
                  <Plus className="mr-2 h-4 w-4" />
                  Crear Evento
                </Link>
              </Button>
            </div>

            {filteredEvents.length === 0 ? (
              <Card>
                <CardContent className="py-12 text-center">
                  <p className="text-muted-foreground">
                    {searchQuery
                      ? "No se encontraron eventos que coincidan con tu búsqueda"
                      : "No tienes eventos creados aún"}
                  </p>
                </CardContent>
              </Card>
            ) : (
              <div className="space-y-4">
                {filteredEvents.map((event) => (
                  <Card key={event.id} className="hover:shadow-lg transition-shadow">
                    <CardContent className="p-6">
                      <div className="flex flex-col md:flex-row gap-6">
                        <div className="flex-1 space-y-4">
                          <div className="flex items-start justify-between gap-4">
                            <div>
                              <h3 className="font-bold text-xl mb-2">{event.title}</h3>
                              <Badge className="gradient-brand text-white border-0">{event.category}</Badge>
                            </div>
                          </div>

                          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
                            <div className="flex items-center gap-2 text-muted-foreground">
                              <Calendar className="h-4 w-4 shrink-0" />
                              <span>
                                {new Date(event.date).toLocaleDateString("es-ES")} - {event.time}
                              </span>
                            </div>
                            <div className="flex items-center gap-2 text-muted-foreground">
                              <MapPin className="h-4 w-4 shrink-0" />
                              <span>{event.location}</span>
                            </div>
                            <div className="flex items-center gap-2 text-muted-foreground">
                              <Ticket className="h-4 w-4 shrink-0" />
                              <span>{event.availableTickets} disponibles</span>
                            </div>
                          </div>

                          <div className="flex flex-wrap gap-2">
                            {event.areas.map((area) => (
                              <Badge key={area.id} variant="outline">
                                {area.name}: ${area.price.toLocaleString()}
                              </Badge>
                            ))}
                          </div>
                        </div>

                        <div className="flex md:flex-col gap-2">
                          <Tooltip>
                            <TooltipTrigger asChild>
                              <Button
                                variant="outline"
                                size="icon"
                                className="flex-1 md:flex-none bg-transparent cursor-pointer"
                                asChild
                              >
                                <Link href={`/admin/events/edit/${event.id}`}>
                                  <Edit className="h-4 w-4" />
                                </Link>
                              </Button>
                            </TooltipTrigger>
                            <TooltipContent>
                              <p>Editar evento</p>
                            </TooltipContent>
                          </Tooltip>

                          <Tooltip>
                            <TooltipTrigger asChild>
                              <Button
                                variant="outline"
                                size="icon"
                                className="flex-1 md:flex-none text-destructive hover:text-destructive bg-transparent cursor-pointer"
                                onClick={() => setDeleteEventId(event.id)}
                              >
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            </TooltipTrigger>
                            <TooltipContent>
                              <p>Eliminar evento</p>
                            </TooltipContent>
                          </Tooltip>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </main>
        </div>

        <AlertDialog open={!!deleteEventId} onOpenChange={() => setDeleteEventId(null)}>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>¿Estás seguro?</AlertDialogTitle>
              <AlertDialogDescription>
                Esta acción no se puede deshacer. El evento será eliminado permanentemente junto con todas sus áreas y
                tickets asociados.
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel className="cursor-pointer">Cancelar</AlertDialogCancel>
              <AlertDialogAction
                className="bg-destructive text-destructive-foreground hover:bg-destructive/90 cursor-pointer"
                onClick={() => deleteEventId && handleDeleteEvent(deleteEventId)}
              >
                Eliminar
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </TooltipProvider>
  )
}
