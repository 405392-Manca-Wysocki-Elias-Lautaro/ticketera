"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Calendar, DollarSign, Ticket, Users, Plus, TrendingUp, Package, Award, TrendingDown, Star } from "lucide-react"
import Link from "next/link"
import { useAuth } from '@/hooks/auth/useAuth'
import GradientText from '@/components/GradientText'
import { RoleUtils } from '@/utils/roleUtils'
import StarBorder from '@/components/StarBorder'
import { eventService } from '@/services/eventService'
import type { OrganizerMetrics } from '@/types/OrganizerMetrics'

export default function AdminDashboardPage() {
    const router = useRouter()
    const { user, isLoading, token } = useAuth()
    const [metrics, setMetrics] = useState<OrganizerMetrics | null>(null)
    const [isLoadingMetrics, setIsLoadingMetrics] = useState(true)

    useEffect(() => {
        if (!isLoading && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoading, router])

    useEffect(() => {
        async function fetchMetrics() {
            // Verificar que tenemos token y usuario antes de hacer la petición
            if (!user || !RoleUtils.isAdmin(user) || !token) return;
            
            try {
                setIsLoadingMetrics(true);
                const data = await eventService.getOrganizerMetrics();
                setMetrics(data);
            } catch (error) {
                console.error("Error loading metrics:", error);
            } finally {
                setIsLoadingMetrics(false);
            }
        }

        // Solo hacer fetch cuando tengamos token, usuario y no esté cargando
        if (!isLoading && user && token) {
            fetchMetrics();
        }
    }, [user, isLoading, token])

    if (isLoading || !user || !RoleUtils.isAdmin(user)) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="flex h-screen overflow-auto">
            <div className="flex-1">
                <main className="container mx-auto px-4 py-8">
                    <div className="flex items-center justify-between mb-8">
                        <div>
                            <GradientText>
                                <h1 className="text-3xl font-bold">Panel de Administración</h1>
                            </GradientText>
                            <p className="text-muted-foreground">Métricas Generales</p>
                        </div>
                        <StarBorder>
                            <Button asChild className="gradient-brand text-white cursor-pointer">
                                <Link href="/admin/events/create">
                                    <Plus className="mr-2 h-4 w-4" />
                                    Crear Evento
                                </Link>
                            </Button>
                        </StarBorder>
                    </div>

                    {/* Stats - Layout reorganizado */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-16">
                        {/* Columna Izquierda */}
                        <div className="space-y-6">
                            {/* Tickets - Arriba */}
                            <div className="space-y-6">
                                <Card>
                                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                                        <CardTitle className="text-sm font-medium text-muted-foreground">Tickets Disponibles</CardTitle>
                                        <Package className="h-4 w-4 text-muted-foreground" />
                                    </CardHeader>
                                    <CardContent>
                                        {isLoadingMetrics ? (
                                            <div className="h-8 w-16 animate-pulse bg-muted rounded" />
                                        ) : (
                                            <>
                                                <div className="text-2xl font-bold">
                                                    {metrics?.availableTickets?.toLocaleString() || 0}
                                                </div>
                                                <p className="text-xs text-muted-foreground">Total de tickets disponibles</p>
                                            </>
                                        )}
                                    </CardContent>
                                </Card>

                                <Card>
                                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                                        <CardTitle className="text-sm font-medium text-muted-foreground">Tickets Vendidos</CardTitle>
                                        <Ticket className="h-4 w-4 text-muted-foreground" />
                                    </CardHeader>
                                    <CardContent>
                                        {isLoadingMetrics ? (
                                            <div className="h-8 w-16 animate-pulse bg-muted rounded" />
                                        ) : (
                                            <>
                                                <div className="text-2xl font-bold">
                                                    {metrics?.totalTicketsSold?.toLocaleString() || 0}
                                                </div>
                                                <p className="text-xs text-muted-foreground">Total de tickets vendidos</p>
                                            </>
                                        )}
                                    </CardContent>
                                </Card>

                                <Card>
                                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                                        <CardTitle className="text-sm font-medium text-muted-foreground">Tipo Más Vendido</CardTitle>
                                        <Star className="h-4 w-4 text-muted-foreground" />
                                    </CardHeader>
                                    <CardContent>
                                        {isLoadingMetrics ? (
                                            <div className="h-8 w-16 animate-pulse bg-muted rounded" />
                                        ) : (
                                            <>
                                                <div className="text-2xl font-bold">
                                                    {metrics?.mostSoldTicketTypeName || "N/A"}
                                                </div>
                                                <p className="text-xs text-muted-foreground">Tipo de ticket más vendido</p>
                                            </>
                                        )}
                                    </CardContent>
                                </Card>
                            </div>

                            {/* Ingresos - Abajo */}
                            <div className="space-y-6">
                                <Card>
                                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                                        <CardTitle className="text-sm font-medium text-muted-foreground">Ingresos Totales</CardTitle>
                                        <DollarSign className="h-4 w-4 text-muted-foreground" />
                                    </CardHeader>
                                    <CardContent>
                                        {isLoadingMetrics ? (
                                            <div className="h-8 w-24 animate-pulse bg-muted rounded" />
                                        ) : (
                                            <>
                                                <div className="text-2xl font-bold">
                                                    ${metrics?.totalRevenue?.toLocaleString('es-AR', {
                                                        minimumFractionDigits: 0,
                                                        maximumFractionDigits: 0
                                                    }) || 0}
                                                </div>
                                                <p className="text-xs text-muted-foreground">Total acumulado</p>
                                            </>
                                        )}
                                    </CardContent>
                                </Card>
                            </div>
                        </div>

                        {/* Columna Derecha - Eventos */}
                        <div className="space-y-6">
                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between pb-2">
                                    <CardTitle className="text-sm font-medium text-muted-foreground">Eventos Activos</CardTitle>
                                    <Calendar className="h-4 w-4 text-muted-foreground" />
                                </CardHeader>
                                <CardContent>
                                    {isLoadingMetrics ? (
                                        <div className="h-8 w-16 animate-pulse bg-muted rounded" />
                                    ) : (
                                        <>
                                            <div className="text-2xl font-bold">
                                                {metrics?.activeEventsCount?.toLocaleString() || 0}
                                            </div>
                                            <p className="text-xs text-muted-foreground">Eventos activos</p>
                                        </>
                                    )}
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between pb-2">
                                    <CardTitle className="text-sm font-medium text-muted-foreground">Evento Más Popular</CardTitle>
                                    <Award className="h-4 w-4 text-muted-foreground" />
                                </CardHeader>
                                <CardContent>
                                    {isLoadingMetrics ? (
                                        <div className="h-8 w-full animate-pulse bg-muted rounded" />
                                    ) : (
                                        <>
                                            <div className="text-xl font-bold break-words leading-tight">
                                                {metrics?.mostPopularEventName || "N/A"}
                                            </div>
                                            <p className="text-xs text-muted-foreground mt-2">Mayor venta de entradas</p>
                                        </>
                                    )}
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between pb-2">
                                    <CardTitle className="text-sm font-medium text-muted-foreground">Mayor Recaudación</CardTitle>
                                    <TrendingUp className="h-4 w-4 text-muted-foreground" />
                                </CardHeader>
                                <CardContent>
                                    {isLoadingMetrics ? (
                                        <div className="h-8 w-full animate-pulse bg-muted rounded" />
                                    ) : (
                                        <>
                                            <div className="text-xl font-bold break-words leading-tight">
                                                {metrics?.mostProfitableEventName || "N/A"}
                                            </div>
                                            <p className="text-xs text-muted-foreground mt-2">Evento con mayor recaudación</p>
                                        </>
                                    )}
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="flex flex-row items-center justify-between pb-2">
                                    <CardTitle className="text-sm font-medium text-muted-foreground">Ventas por semana</CardTitle>
                                    <Users className="h-4 w-4 text-muted-foreground" />
                                </CardHeader>
                                <CardContent>
                                    {isLoadingMetrics ? (
                                        <div className="h-8 w-16 animate-pulse bg-muted rounded" />
                                    ) : (
                                        <>
                                            <div className="text-2xl font-bold">
                                                {metrics?.ticketsSoldLastWeek?.toLocaleString() || 0}
                                            </div>
                                            <p className="text-xs text-muted-foreground">Tickets últimos 7 días</p>
                                        </>
                                    )}
                                </CardContent>
                            </Card>
                        </div>
                    </div>

                    {/* Quick Actions */}
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-12">
                        <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                            <Link href="/admin/events">
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <Calendar className="h-5 w-5 text-primary" />
                                        Mis Eventos
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-muted-foreground">Ver y gestionar todos tus eventos publicados</p>
                                </CardContent>
                            </Link>
                        </Card>

                        <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                            <Link href="/admin/payments">
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <DollarSign className="h-5 w-5 text-primary" />
                                        Pagos
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-muted-foreground">Revisa pagos completados y pendientes</p>
                                </CardContent>
                            </Link>
                        </Card>

                        <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                            <Link href="/profile">
                                <CardHeader>
                                    <CardTitle className="flex items-center gap-2">
                                        <Users className="h-5 w-5 text-primary" />
                                        Configuración
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-muted-foreground">Administra tu cuenta y preferencias</p>
                                </CardContent>
                            </Link>
                        </Card>
                    </div>
                </main>
            </div>
        </div>
    )
}
