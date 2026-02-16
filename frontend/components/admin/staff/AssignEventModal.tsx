"use client"

import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { eventService, EventInfo } from "@/services/eventService"
import { User } from "@/types/User"
import { SpinnerOverlay } from "@/components/SpinnerOverlay"
import { CheckCircle2, Ticket } from "lucide-react"

interface AssignEventModalProps {
    isOpen: boolean
    onClose: () => void
    staffMember: User | null
}

export function AssignEventModal({ isOpen, onClose, staffMember }: AssignEventModalProps) {
    const [events, setEvents] = useState<EventInfo[]>([])
    const [loading, setLoading] = useState(false)
    const [assigning, setAssigning] = useState(false)
    const [selectedEventId, setSelectedEventId] = useState<string>("")
    const [assignedEventIds, setAssignedEventIds] = useState<Set<string>>(new Set())

    useEffect(() => {
        if (isOpen && staffMember) {
            fetchEventsAndAssignments()
        }
    }, [isOpen, staffMember])

    const fetchEventsAndAssignments = async () => {
        setLoading(true)
        try {
            // 1. Fetch organization events
            const eventsRes = await eventService.getMyOrganizationEvents()
            const orgEvents = eventsRes.data.data || []
            setEvents(orgEvents)

            // 2. Fetch assignments for each event to check if this user is assigned
            // This is N+1, ideally we should have an endpoint to get "events assigned to user"
            // or "staff assigned to event". 
            // Better approach: When selecting an event, we check if user is already assigned?
            // Or we just fetch all events and let backend handle "already assigned" gracefully.
            // But to show "Already assigned" in UI we need context.
            // Since we don't have "getEventsForStaffUser(userId)", we can't easily filter.
            // Let's iterate categories or just show all events.

            // Optimization: We could fetch "getEventStaff" for each event but that's heavy.
            // Let's skip checking *pre-assigned* status for now to save bandwidth, 
            // or maybe just rely on error "Already assigned" if backend returns it?
            // Backend `assignStaff` returns void, and if exists just returns.

            // Check if backend `getEventsForStaff` (which is `getEventsByOrganizerId` for staff context) 
            // helps? No, that's for the logged in staff.

            // We will just list events. If user selects one, we assign.
        } catch (error) {
            console.error("Error fetching events", error)
        } finally {
            setLoading(false)
        }
    }

    const handleAssign = async () => {
        if (!selectedEventId || !staffMember) return

        setAssigning(true)
        try {
            await eventService.assignStaff(selectedEventId, staffMember.id)
            // Show success or close
            onClose()
            // Optional: toast success
        } catch (error) {
            console.error("Error assigning staff", error)
        } finally {
            setAssigning(false)
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Asignar Evento a {staffMember?.firstName} {staffMember?.lastName}</DialogTitle>
                </DialogHeader>

                {loading ? (
                    <div className="py-8"><SpinnerOverlay /></div>
                ) : (
                    <div className="space-y-4 py-4">
                        <div className="space-y-2">
                            <Label>Seleccionar Evento</Label>
                            <div className="grid gap-2 max-h-[300px] overflow-y-auto">
                                {events.map((event) => (
                                    <div
                                        key={event.id}
                                        className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer transition-colors ${selectedEventId === event.id ? "border-primary bg-primary/5" : "hover:bg-muted"
                                            }`}
                                        onClick={() => setSelectedEventId(event.id)}
                                    >
                                        <div className="flex items-center gap-3">
                                            <div className="p-2 bg-muted rounded-full">
                                                <Ticket className="w-4 h-4 text-muted-foreground" />
                                            </div>
                                            <div>
                                                <p className="font-medium text-sm">{event.title}</p>
                                                <p className="text-xs text-muted-foreground">
                                                    {event.startsAt ? new Date(event.startsAt).toLocaleDateString() : 'Fecha TBD'}
                                                </p>
                                            </div>
                                        </div>
                                        {selectedEventId === event.id && (
                                            <CheckCircle2 className="w-5 h-5 text-primary" />
                                        )}
                                    </div>
                                ))}
                                {events.length === 0 && (
                                    <p className="text-center text-muted-foreground text-sm py-4">
                                        No hay eventos disponibles.
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>
                )}

                <DialogFooter>
                    <Button variant="outline" onClick={onClose}>Cancelar</Button>
                    <Button onClick={handleAssign} disabled={!selectedEventId || assigning || loading}>
                        {assigning ? "Asignando..." : "Confirmar Asignación"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}
