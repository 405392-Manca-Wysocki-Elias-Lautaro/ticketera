"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/hooks/auth/useAuth"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { DollarSign, Clock, CheckCircle2 } from "lucide-react"
import { Navbar } from '@/components/Navbar'

interface Payment {
  id: string
  eventTitle: string
  amount: number
  date: string
  status: "completed" | "pending"
  buyer: string
}

const mockPayments: Payment[] = [
  {
    id: "1",
    eventTitle: "Festival de Rock 2025",
    amount: 15000,
    date: "2025-10-15",
    status: "completed",
    buyer: "Juan Pérez",
  },
  {
    id: "2",
    eventTitle: "Stand Up Comedy Night",
    amount: 7000,
    date: "2025-10-14",
    status: "completed",
    buyer: "María García",
  },
  {
    id: "3",
    eventTitle: "Conferencia Tech Summit",
    amount: 24000,
    date: "2025-10-16",
    status: "pending",
    buyer: "Carlos López",
  },
]

export default function AdminPaymentsPage() {
  const router = useRouter()
  const { user, isLoading } = useAuth()

  useEffect(() => {
    if (!isLoading && (!user || user.role !== "admin")) {
      router.push("/dashboard")
    }
  }, [user, isLoading, router])

  if (isLoading || !user || user.role !== "admin") {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  const completedPayments = mockPayments.filter((p) => p.status === "completed")
  const pendingPayments = mockPayments.filter((p) => p.status === "pending")

  const totalCompleted = completedPayments.reduce((sum, p) => sum + p.amount, 0)
  const totalPending = pendingPayments.reduce((sum, p) => sum + p.amount, 0)

  return (
    <div className="min-h-screen bg-background">
      <Navbar />

      <main className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold mb-8 gradient-text">Pagos</h1>

        {/* Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total Completado</CardTitle>
              <CheckCircle2 className="h-4 w-4 text-green-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold gradient-text">${totalCompleted.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground">{completedPayments.length} transacciones</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Pendiente</CardTitle>
              <Clock className="h-4 w-4 text-yellow-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${totalPending.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground">{pendingPayments.length} transacciones</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">Total General</CardTitle>
              <DollarSign className="h-4 w-4 text-primary" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${(totalCompleted + totalPending).toLocaleString()}</div>
              <p className="text-xs text-muted-foreground">{mockPayments.length} transacciones</p>
            </CardContent>
          </Card>
        </div>

        {/* Payments List */}
        <Tabs defaultValue="completed" className="space-y-6">
          <TabsList>
            <TabsTrigger value="completed">Completados ({completedPayments.length})</TabsTrigger>
            <TabsTrigger value="pending">Pendientes ({pendingPayments.length})</TabsTrigger>
          </TabsList>

          <TabsContent value="completed" className="space-y-4">
            {completedPayments.map((payment) => (
              <PaymentCard key={payment.id} payment={payment} />
            ))}
          </TabsContent>

          <TabsContent value="pending" className="space-y-4">
            {pendingPayments.map((payment) => (
              <PaymentCard key={payment.id} payment={payment} />
            ))}
          </TabsContent>
        </Tabs>
      </main>
    </div>
  )
}

function PaymentCard({ payment }: { payment: Payment }) {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="space-y-1">
            <h3 className="font-semibold">{payment.eventTitle}</h3>
            <p className="text-sm text-muted-foreground">Comprador: {payment.buyer}</p>
            <p className="text-xs text-muted-foreground">
              {new Date(payment.date).toLocaleDateString("es-ES", {
                day: "numeric",
                month: "long",
                year: "numeric",
              })}
            </p>
          </div>

          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="text-2xl font-bold gradient-text">${payment.amount.toLocaleString()}</p>
            </div>
            <Badge
              variant={payment.status === "completed" ? "default" : "secondary"}
              className={
                payment.status === "completed" ? "bg-green-500 hover:bg-green-600" : "bg-yellow-500 hover:bg-yellow-600"
              }
            >
              {payment.status === "completed" ? "Completado" : "Pendiente"}
            </Badge>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
