"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { QrCode, Settings, Home, Calendar } from "lucide-react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"

export function StaffSidebar() {
  const pathname = usePathname()

  const links = [
    {
      title: "Validar Tickets",
      href: "/staff",
      icon: QrCode,
    },
    {
      title: "Mis Eventos Asignados",
      href: "/staff/events",
      icon: Calendar,
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
        <h2 className="text-lg font-semibold gradient-text">Panel Staff</h2>
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
