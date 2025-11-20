"use client"

import Link from "next/link"
import Image from "next/image"
import { useState } from "react"
import { useRouter, usePathname } from "next/navigation"
import { useAuth } from "@/hooks/auth/useAuth"

import {
    Search,
    Ticket,
    User,
    LogOut,
    Settings,
    LayoutDashboard,
    Calendar,
    Home,
    LogIn,
} from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

import {
    Tooltip,
    TooltipContent,
    TooltipTrigger,
} from "@/components/ui/tooltip"

import { RoleUtils } from "@/utils/roleUtils"
import GradientText from "./GradientText"

interface NavbarProps {
    leftSlot?: React.ReactNode
    rightSlot?: React.ReactNode
    hideSearchOn?: string[]       // rutas donde NO debe aparecer
}

export function Navbar({ leftSlot, rightSlot, hideSearchOn = [] }: NavbarProps) {
    const { user, logout } = useAuth()
    const router = useRouter();
    const pathname = usePathname()

    const [searchQuery, setSearchQuery] = useState("")

    // Rutas donde NO mostrar el searchbar (por defecto)
    const defaultHiddenSearchRoutes = ["/staff", "/staff/validate"]
    const hidden = [...defaultHiddenSearchRoutes, ...hideSearchOn]

    const shouldHideSearch = hidden.some((route) =>
        pathname?.startsWith(route)
    )

    const logoHref =
        RoleUtils.isAdmin(user)
            ? "/admin"
            : RoleUtils.isStaff(user)
                ? "/staff"
                : "/dashboard"

    const isAdminPage = pathname?.startsWith("/admin")
    const searchPlaceholder = isAdminPage
        ? "Buscar en mis eventos..."
        : "Buscar eventos..."

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault()
        if (searchQuery.trim()) {
            const searchPath = isAdminPage
                ? `/admin/events?search=${encodeURIComponent(searchQuery)}`
                : `/dashboard?search=${encodeURIComponent(searchQuery)}`

            router.push(searchPath)
        }
    }

    return (
        <nav className="
        sticky top-0 z-50 w-full border-b 
        bg-background/95 backdrop-blur 
        supports-backdrop-filter:bg-background/60
    ">
            <div className="container mx-auto px-4">
                <div className="flex h-16 items-center justify-between gap-4">

                    {/* LEFT SIDE: SidebarTrigger (mobile) + Logo */}
                    <div className="flex items-center gap-2 shrink-0">
                        {leftSlot}

                        <Link href={"/app" + logoHref} className="flex items-center gap-2 shrink-0">
                            <Image
                                src="/logo.png"
                                alt="Ticketly"
                                width={32}
                                height={32}
                                className="h-8 w-8 rounded-full"
                            />

                            <GradientText className="backdrop-blur-none">
                                <span className="font-bold text-lg hidden sm:inline">
                                    Ticketly
                                </span>
                            </GradientText>
                        </Link>
                    </div>

                    {/* SEARCHBAR (solo si está permitido) */}
                    {!shouldHideSearch && (
                        <form onSubmit={handleSearch} className="flex-1 max-w-md">
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                <Input
                                    type="search"
                                    placeholder={searchPlaceholder}
                                    className="pl-9 w-full"
                                    value={searchQuery}
                                    onChange={(e) => setSearchQuery(e.target.value)}
                                />
                            </div>
                        </form>
                    )}

                    {/* RIGHT SIDE (Tickets + Profile + optional rightSlot) */}
                    <div className="flex items-center gap-2">
                        {user && (
                            <Tooltip>
                                <TooltipTrigger asChild>
                                    <Button variant="ghost" size="icon" asChild className="shrink-0">
                                        <Link href="/app/my-tickets">
                                            <Ticket className="h-5 w-5" />
                                            <span className="sr-only">Mis Tickets</span>
                                        </Link>
                                    </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    <p>Mis Tickets</p>
                                </TooltipContent>
                            </Tooltip>
                        )}

                        {rightSlot}

                        {/* MENU USUARIO */}
                        <DropdownMenu>
                            <Tooltip>
                                <TooltipTrigger asChild>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" size="icon" className="shrink-0">
                                            <User className="h-5 w-5" />
                                            <span className="sr-only">Menú de usuario</span>
                                        </Button>
                                    </DropdownMenuTrigger>
                                </TooltipTrigger>

                                <TooltipContent>
                                    <p>Menú de usuario</p>
                                </TooltipContent>
                            </Tooltip>

                            <DropdownMenuContent align="end" className="w-56 z-[9999]">
                                {user ? (
                                    <>
                                        <div className="px-2 py-1.5">
                                            <p className="text-sm font-medium">{user?.firstName}</p>
                                            <p className="text-xs text-muted-foreground">{user?.email}</p>
                                        </div>

                                        {(RoleUtils.canManageEvents(user)) && (
                                            <>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/app/dashboard">
                                                        <Home className="mr-2 h-4 w-4" />
                                                        Ver Eventos Públicos
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                            </>
                                        )}

                                        {RoleUtils.isAdmin(user) && (
                                            <>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/app/admin">
                                                        <LayoutDashboard className="mr-2 h-4 w-4" />
                                                        Panel Admin
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/app/admin/events">
                                                        <Calendar className="mr-2 h-4 w-4" />
                                                        Mis Eventos
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                            </>
                                        )}

                                        {RoleUtils.isStaff(user) && (
                                            <>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/app/staff">
                                                        <LayoutDashboard className="mr-2 h-4 w-4" />
                                                        Panel Staff
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                            </>
                                        )}

                                        <DropdownMenuItem asChild>
                                            <Link href="/app/profile">
                                                <Settings className="mr-2 h-4 w-4" />
                                                Configuración
                                            </Link>
                                        </DropdownMenuItem>

                                        <DropdownMenuItem
                                            onClick={() => logout()}
                                            className="text-destructive"
                                        >
                                            <LogOut className="mr-2 h-4 w-4" />
                                            Cerrar Sesión
                                        </DropdownMenuItem>
                                    </>
                                ) : (
                                    <DropdownMenuItem
                                        onClick={() => router.push("/app/login")}
                                        className="text-info"
                                    >
                                        <LogIn className="mr-2 h-4 w-4" />
                                        Iniciar Sesión
                                    </DropdownMenuItem>
                                )}
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </div>
            </div>
        </nav>
    )
}
