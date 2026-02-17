"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Badge } from "@/components/ui/badge"
import { Loader2, Plus, Trash2, UserPlus } from "lucide-react"
import { User } from "@/types/User"
import { authService } from "@/services/authService"
import { eventService } from "@/services/eventService"
import { toast } from "sonner"
import { Separator } from "@/components/ui/separator"

interface EventStaffManagerProps {
    eventId: string
}

export function EventStaffManager({ eventId }: EventStaffManagerProps) {
    const [allStaff, setAllStaff] = useState<User[]>([])
    const [assignedStaffIds, setAssignedStaffIds] = useState<string[]>([])
    const [loading, setLoading] = useState(true)
    const [assigning, setAssigning] = useState(false)
    const [selectedStaffId, setSelectedStaffId] = useState<string>("")

    useEffect(() => {
        fetchData()
    }, [eventId])

    const fetchData = async () => {
        setLoading(true)
        try {
            const [staffRes, assignedRes] = await Promise.all([
                authService.getStaff(),
                eventService.getEventStaff(eventId)
            ])

            setAllStaff(staffRes.data.data || [])
            setAssignedStaffIds(assignedRes.data.data || [])
        } catch (error) {
            console.error("Error fetching staff data", error)
            toast.error("Error al cargar información del staff")
        } finally {
            setLoading(false)
        }
    }

    const assignedStaff = allStaff.filter(user => assignedStaffIds.includes(user.id))
    const availableStaff = allStaff.filter(user => !assignedStaffIds.includes(user.id))

    const handleAssign = async () => {
        if (!selectedStaffId) return

        setAssigning(true)
        try {
            await eventService.assignStaff(eventId, selectedStaffId)
            setAssignedStaffIds(prev => [...prev, selectedStaffId])
            setSelectedStaffId("")
            toast.success("Staff asignado correctamente")
        } catch (error) {
            console.error("Error assigning staff", error)
            toast.error("Error al asignar staff")
        } finally {
            setAssigning(false)
        }
    }

    const handleRemove = async (userId: string) => {
        try {
            await eventService.removeStaff(eventId, userId)
            setAssignedStaffIds(prev => prev.filter(id => id !== userId))
            toast.success("Staff removido correctamente")
        } catch (error) {
            console.error("Error removing staff", error)
            toast.error("Error al remover staff")
        }
    }

    if (loading) {
        return (
            <Card>
                <CardContent className="py-8 flex justify-center">
                    <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </CardContent>
            </Card>
        )
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>Staff del Evento</CardTitle>
                <CardDescription>Administra el personal asignado a este evento</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
                {/* Section: Assign New Staff */}
                <div className="flex gap-4 items-end">
                    <div className="flex-1 space-y-2">
                        <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
                            Asignar nuevo miembro
                        </label>
                        <Select value={selectedStaffId} onValueChange={setSelectedStaffId}>
                            <SelectTrigger>
                                <SelectValue placeholder="Seleccionar staff..." />
                            </SelectTrigger>
                            <SelectContent>
                                {availableStaff.length === 0 ? (
                                    <SelectItem value="none" disabled>No hay staff disponible</SelectItem>
                                ) : (
                                    availableStaff.map(user => (
                                        <SelectItem key={user.id} value={user.id}>
                                            {user.firstName} {user.lastName} ({user.email})
                                        </SelectItem>
                                    ))
                                )}
                            </SelectContent>
                        </Select>
                    </div>
                    <Button
                        type="button"
                        onClick={handleAssign}
                        disabled={!selectedStaffId || assigning}
                        className="mb-[2px]"
                    >
                        {assigning ? <Loader2 className="h-4 w-4 animate-spin" /> : <UserPlus className="h-4 w-4 mr-2" />}
                        Asignar
                    </Button>
                </div>

                <Separator />

                {/* Section: Assigned Staff List */}
                <div className="space-y-4">
                    <h4 className="text-sm font-semibold">Personal Asignado ({assignedStaff.length})</h4>

                    {assignedStaff.length === 0 ? (
                        <p className="text-sm text-muted-foreground italic">No hay personal asignado a este evento.</p>
                    ) : (
                        <div className="grid gap-3">
                            {assignedStaff.map(user => (
                                <div key={user.id} className="flex items-center justify-between p-3 border rounded-lg bg-card">
                                    <div className="flex flex-col">
                                        <span className="font-medium">{user.firstName} {user.lastName}</span>
                                        <span className="text-xs text-muted-foreground">{user.email}</span>
                                    </div>
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="sm"
                                        className="text-destructive hover:text-destructive hover:bg-destructive/10"
                                        onClick={() => handleRemove(user.id)}
                                    >
                                        <Trash2 className="h-4 w-4" />
                                    </Button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </CardContent>
        </Card>
    )
}
