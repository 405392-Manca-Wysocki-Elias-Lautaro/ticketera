// sidebar.config.ts
import {
    Calendar,
    LayoutDashboard,
    Plus,
    Settings,
    TicketIcon,
    QrCode,
    UserCog,
} from "lucide-react"

export const SidebarConfig = {
    admin: [
        { title: "Dashboard", href: "/admin", icon: LayoutDashboard },
        { title: "Mis Eventos", href: "/admin/events", icon: Calendar },
        { title: "Crear Evento", href: "/admin/events/create", icon: Plus },
        { title: "Gestionar Staff", href: "/admin/staff", icon: UserCog },
        { title: "Validar Tickets", href: "/admin/validate", icon: TicketIcon },
        { title: "Configuración", href: "/admin/settings", icon: Settings },
    ],

    staff: [
        { title: "Validar Tickets", href: "/staff", icon: QrCode },
        { title: "Mis Eventos Asignados", href: "/staff/events", icon: Calendar },
        { title: "Configuración", href: "/profile", icon: Settings },
    ],
}
