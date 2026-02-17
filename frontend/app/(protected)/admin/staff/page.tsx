"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import GradientText from "@/components/GradientText"
import { useAuth } from "@/hooks/auth/useAuth"
import { User } from "@/types/User"
import { authService } from "@/services/authService"
import { SpinnerOverlay } from "@/components/SpinnerOverlay"
import { AssignEventModal } from "@/components/admin/staff/AssignEventModal"
import { RoleUtils } from "@/utils/roleUtils"
import { useRouter } from "next/navigation"
import { Plus, UserCog, Trash2 } from "lucide-react"
import { AddStaffModal } from "@/components/admin/staff/AddStaffModal"

export default function AdminStaffPage() {
    const { user, isLoading: isAuthLoading } = useAuth()
    const router = useRouter()

    const [staff, setStaff] = useState<User[]>([])
    const [loading, setLoading] = useState(true)
    const [selectedStaff, setSelectedStaff] = useState<User | null>(null)
    const [isAssignModalOpen, setIsAssignModalOpen] = useState(false)
    const [isAddModalOpen, setIsAddModalOpen] = useState(false)

    useEffect(() => {
        if (!isAuthLoading && user) {
            if (!RoleUtils.isAdmin(user) && !RoleUtils.isSuperAdmin(user)) {
                router.push("/dashboard")
                return
            }
            fetchStaff()
        }
    }, [user, isAuthLoading, router])

    const fetchStaff = async () => {
        setLoading(true)
        try {
            const response = await authService.getStaff()
            setStaff(response.data.data)
        } catch (error) {
            console.error("Error fetching staff", error)
        } finally {
            setLoading(false)
        }
    }

    const openAssignModal = (staffMember: User) => {
        setSelectedStaff(staffMember)
        setIsAssignModalOpen(true)
    }

    if (isAuthLoading || loading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <SpinnerOverlay />
            </div>
        )
    }

    return (
        <div className="container mx-auto py-8 space-y-8">
            <div className="flex justify-between items-center">
                <GradientText>
                    <h1 className="text-3xl font-bold">Gestión de Staff</h1>
                </GradientText>
                <Button onClick={() => setIsAddModalOpen(true)}>
                    <Plus className="mr-2 h-4 w-4" /> Nuevo Staff
                </Button>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Miembros del Staff</CardTitle>
                </CardHeader>
                <CardContent className="p-6">
                    <Table>
                        <TableCaption>Lista de miembros del staff asociados a tu organización.</TableCaption>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nombre</TableHead>
                                <TableHead>Email</TableHead>
                                <TableHead>Rol</TableHead>
                                <TableHead className="text-right">Acciones</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {staff.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                                        No hay miembros de staff registrados.
                                    </TableCell>
                                </TableRow>
                            ) : (
                                staff.map((member) => (
                                    <TableRow key={member.id}>
                                        <TableCell className="font-medium">{member.firstName} {member.lastName}</TableCell>
                                        <TableCell>{member.email}</TableCell>
                                        <TableCell>{member.role?.name}</TableCell>
                                        <TableCell className="text-right">
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() => openAssignModal(member)}
                                                className="mr-2"
                                            >
                                                <UserCog className="mr-2 h-4 w-4" />
                                                Asignar Evento
                                            </Button>
                                            {/* Delete button could go here */}
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <AssignEventModal
                isOpen={isAssignModalOpen}
                onClose={() => setIsAssignModalOpen(false)}
                staffMember={selectedStaff}
            />

            <AddStaffModal
                isOpen={isAddModalOpen}
                onClose={() => setIsAddModalOpen(false)}
                onSuccess={fetchStaff}
            />
        </div>
    )
}
