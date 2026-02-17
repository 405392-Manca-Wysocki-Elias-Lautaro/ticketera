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
    const [assignedEventIds, setAssignedEventIds] = useState<Set<string>>(new Set())

    useEffect(() => {
        if (isOpen && staffMember) {
            fetchEventsAndAssignments()
        }
    }, [isOpen, staffMember])

    const fetchEventsAndAssignments = async () => {
        setLoading(true)
        try {
            // 1. Fetch all events (consistent with "Mis Eventos" page)
            const eventsRes = await eventService.getAll()
            const orgEvents = eventsRes.data.data || []
            setEvents(orgEvents)

            // 2. Fetch current staff assignments
            if (staffMember) {
                const assignmentsRes = await eventService.getStaffAssignments(staffMember.id);
                const assignedEvents = assignmentsRes.data.data || [];
                const assignedIds = new Set(assignedEvents.map(e => e.id));
                setAssignedEventIds(assignedIds);
            }
        } catch (error) {
            console.error("Error fetching events", error)
        } finally {
            setLoading(false)
        }
    }

    const handleAssign = async () => {
        if (!staffMember) return

        setAssigning(true)
        try {
            const eventIds = Array.from(assignedEventIds);
            await eventService.updateStaffAssignments(staffMember.id, eventIds)
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
                    <DialogTitle>Gestionar Eventos de {staffMember?.firstName} {staffMember?.lastName}</DialogTitle>
                </DialogHeader>

                {loading ? (
                    <div className="py-8"><SpinnerOverlay /></div>
                ) : (
                    <div className="space-y-4 py-4">
                        <div className="space-y-2">
                            <Label>Seleccionar Eventos</Label>
                            <div className="grid gap-2 max-h-[300px] overflow-y-auto">
                                {events.map((event) => {
                                    const isSelected = assignedEventIds.has(event.id);
                                    return (
                                        <div
                                            key={event.id}
                                            className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer transition-colors ${isSelected ? "border-primary bg-primary/5" : "hover:bg-muted"
                                                }`}
                                            onClick={() => {
                                                const newSet = new Set(assignedEventIds);
                                                if (newSet.has(event.id)) {
                                                    newSet.delete(event.id);
                                                } else {
                                                    newSet.add(event.id);
                                                }
                                                setAssignedEventIds(newSet);
                                            }}
                                        >
                                            <div className="flex items-center gap-3">
                                                <div className={`flex items-center justify-center w-5 h-5 rounded border ${isSelected ? "bg-primary border-primary text-primary-foreground" : "border-muted-foreground"}`}>
                                                    {isSelected && <CheckCircle2 className="w-3 h-3" />}
                                                </div>
                                                <div>
                                                    <p className="font-medium text-sm">{event.title}</p>
                                                    <p className="text-xs text-muted-foreground">
                                                        {event.startsAt ? new Date(event.startsAt).toLocaleDateString() : 'Fecha TBD'}
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })}
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
                    <Button onClick={handleAssign} disabled={assigning || loading}>
                        {assigning ? "Guardando..." : "Guardar Cambios"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}
