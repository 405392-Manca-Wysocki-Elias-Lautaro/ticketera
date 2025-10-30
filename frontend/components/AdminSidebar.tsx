"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { Calendar, DollarSign, LayoutDashboard, Plus, Settings, Home } from "lucide-react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"

export function AdminSidebar() {
  const pathname = usePathname()

  const links = [
    {
      title: "Dashboard",
      href: "/admin",
      icon: LayoutDashboard,
    },
    {
      title: "Mis Eventos",
      href: "/admin/events",
      icon: Calendar,
    },
    {
      title: "Crear Evento",
      href: "/admin/events/create",
      icon: Plus,
    },
    {
      title: "Pagos",
      href: "/admin/payments",
      icon: DollarSign,
    },
    {
      title: "Configuración",
      href: "/profile",
      icon: Settings,
    },
  ]

  return (
    <div className="flex h-full w-64 flex-col border-r bg-background">
      <div className="p-6">
        <h2 className="text-lg font-semibold gradient-text">Panel Admin</h2>
      </div>
      <Separator />
      <nav className="flex-1 space-y-1 p-4">
        <Button variant="ghost" asChild className="w-full justify-start mb-2 cursor-pointer">
          <Link href="/dashboard">
            <Home className="mr-2 h-4 w-4" />
            Ver Eventos Públicos
          </Link>
        </Button>
        <Separator className="my-2" />
        {links.map((link) => (
          <Button
            key={link.href}
            variant={pathname === link.href ? "secondary" : "ghost"}
            asChild
            className={cn("w-full justify-start cursor-pointer", pathname === link.href && "bg-accent")}
          >
            <Link href={link.href}>
              <link.icon className="mr-2 h-4 w-4" />
              {link.title}
            </Link>
          </Button>
        ))}
      </nav>
    </div>
  )
}
