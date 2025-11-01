"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Calendar, DollarSign, Ticket, Users, Plus, TrendingUp } from "lucide-react"
import Link from "next/link"
import { Bar, BarChart, Line, LineChart, ResponsiveContainer, XAxis, YAxis, CartesianGrid, Legend } from "recharts"
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { useAuth } from '@/hooks/auth/useAuth'
import { AdminSidebar } from '@/components/AdminSidebar'
import { RoleCode } from '@/types/enums/RoleCode'

const salesData = [
    { month: "Ene", ventas: 12000, tickets: 240 },
    { month: "Feb", ventas: 19000, tickets: 380 },
    { month: "Mar", ventas: 15000, tickets: 300 },
    { month: "Abr", ventas: 25000, tickets: 500 },
    { month: "May", ventas: 22000, tickets: 440 },
    { month: "Jun", ventas: 30000, tickets: 600 },
]

const eventPerformance = [
    { evento: "Rock Fest", vendidos: 450, total: 500 },
    { evento: "Jazz Night", vendidos: 180, total: 200 },
    { evento: "Pop Concert", vendidos: 380, total: 400 },
    { evento: "EDM Party", vendidos: 290, total: 300 },
]

export default function AdminDashboardPage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()

    useEffect(() => {
        if (!isLoading && (!user || user.role.code !== RoleCode.ADMIN)) {
            router.push("/dashboard")
        }
    }, [user, isLoading, router])

    if (isLoading || !user || user.role.code !== RoleCode.ADMIN) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="flex min-h-screen">
            <AdminSidebar />

            <div className="flex-1 overflow-auto">
                <main className="container mx-auto px-4 py-8">
                    <div className="flex items-center justify-between mb-8">
                        <div>
                            <h1 className="text-3xl font-bold gradient-text">Panel de Administración</h1>
                            <p className="text-muted-foreground">Gestiona tus eventos y ventas</p>
                        </div>
                        <Button asChild className="gradient-brand text-white cursor-pointer">
                            <Link href="/admin/events/create">
                                <Plus className="mr-2 h-4 w-4" />
                                Crear Evento
                            </Link>
                        </Button>
                    </div>

                    {/* Stats */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
                        <Card>
                            <CardHeader className="flex flex-row items-center justify-between pb-2">
                                <CardTitle className="text-sm font-medium text-muted-foreground">Eventos Activos</CardTitle>
                                <Calendar className="h-4 w-4 text-muted-foreground" />
                            </CardHeader>
                            <CardContent>
                                <div className="text-2xl font-bold">12</div>
                                <p className="text-xs text-green-500 flex items-center gap-1">
                                    <TrendingUp className="h-3 w-3" />
                                    +2 este mes
                                </p>
                            </CardContent>
                        </Card>

                        <Card>
                            <CardHeader className="flex flex-row items-center justify-between pb-2">
                                <CardTitle className="text-sm font-medium text-muted-foreground">Tickets Vendidos</CardTitle>
                                <Ticket className="h-4 w-4 text-muted-foreground" />
                            </CardHeader>
                            <CardContent>
                                <div className="text-2xl font-bold">1,234</div>
                                <p className="text-xs text-green-500 flex items-center gap-1">
                                    <TrendingUp className="h-3 w-3" />
                                    +180 esta semana
                                </p>
                            </CardContent>
                        </Card>

                        <Card>
                            <CardHeader className="flex flex-row items-center justify-between pb-2">
                                <CardTitle className="text-sm font-medium text-muted-foreground">Ingresos Totales</CardTitle>
                                <DollarSign className="h-4 w-4 text-muted-foreground" />
                            </CardHeader>
                            <CardContent>
                                <div className="text-2xl font-bold">$456,789</div>
                                <p className="text-xs text-green-500 flex items-center gap-1">
                                    <TrendingUp className="h-3 w-3" />
                                    +12% vs mes anterior
                                </p>
                            </CardContent>
                        </Card>

                        <Card>
                            <CardHeader className="flex flex-row items-center justify-between pb-2">
                                <CardTitle className="text-sm font-medium text-muted-foreground">Asistentes</CardTitle>
                                <Users className="h-4 w-4 text-muted-foreground" />
                            </CardHeader>
                            <CardContent>
                                <div className="text-2xl font-bold">892</div>
                                <p className="text-xs text-muted-foreground">En eventos activos</p>
                            </CardContent>
                        </Card>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
                        <Card>
                            <CardHeader>
                                <CardTitle>Ventas Mensuales</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ChartContainer
                                    config={{
                                        ventas: {
                                            label: "Ventas ($)",
                                            color: "hsl(var(--chart-1))",
                                        },
                                    }}
                                    className="h-[300px]"
                                >
                                    <ResponsiveContainer width="100%" height="100%">
                                        <LineChart data={salesData}>
                                            <CartesianGrid strokeDasharray="3 3" />
                                            <XAxis dataKey="month" />
                                            <YAxis />
                                            <ChartTooltip content={<ChartTooltipContent />} />
                                            <Line type="monotone" dataKey="ventas" stroke="var(--color-ventas)" strokeWidth={2} />
                                        </LineChart>
                                    </ResponsiveContainer>
                                </ChartContainer>
                            </CardContent>
                        </Card>

                        <Card>
                            <CardHeader>
                                <CardTitle>Rendimiento por Evento</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <ChartContainer
                                    config={{
                                        vendidos: {
                                            label: "Vendidos",
                                            color: "hsl(var(--chart-2))",
                                        },
                                        total: {
                                            label: "Capacidad",
                                            color: "hsl(var(--chart-3))",
                                        },
                                    }}
                                    className="h-[300px]"
                                >
                                    <ResponsiveContainer width="100%" height="100%">
                                        <BarChart data={eventPerformance}>
                                            <CartesianGrid strokeDasharray="3 3" />
                                            <XAxis dataKey="evento" />
                                            <YAxis />
                                            <ChartTooltip content={<ChartTooltipContent />} />
                                            <Legend />
                                            <Bar dataKey="vendidos" fill="var(--color-vendidos)" />
                                            <Bar dataKey="total" fill="var(--color-total)" />
                                        </BarChart>
                                    </ResponsiveContainer>
                                </ChartContainer>
                            </CardContent>
                        </Card>
                    </div>

                    {/* Quick Actions */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
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
