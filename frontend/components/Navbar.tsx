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
import { RoleCode } from '@/types/enums/RoleCode'

export function Navbar() {
    const { user, logout } = useAuth()
    const router = useRouter()
    const pathname = usePathname()
    const [searchQuery, setSearchQuery] = useState("")

    const logoHref =
        user?.role?.code === RoleCode.ADMIN
            ? "/admin"
            : user?.role?.code === RoleCode.STAFF
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
        <nav className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60">
            <div className="container mx-auto px-4">
                <div className="flex h-16 items-center justify-between gap-4">
                    {/* Logo */}
                    <Link href={logoHref} className="flex items-center gap-2 shrink-0">
                        <Image
                            src="/logo.png"
                            alt="Ticketera"
                            width={32}
                            height={32}
                            className="h-8 w-8 rounded-full"
                        />
                        <span className="font-bold text-lg gradient-text hidden sm:inline">
                            Ticketera
                        </span>
                    </Link>

                    {/* Search Bar */}
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

                    {/* Actions */}
                    <div className="flex items-center gap-2">
                        {user &&
                            <Tooltip>
                                <TooltipTrigger asChild>
                                    <Button
                                        variant="ghost"
                                        size="icon"
                                        asChild
                                        className="shrink-0 "
                                    >
                                        <Link href="/my-tickets">
                                            <Ticket className="h-5 w-5" />
                                            <span className="sr-only">Mis Tickets</span>
                                        </Link>
                                    </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                    <p>Mis Tickets</p>
                                </TooltipContent>
                            </Tooltip>
                        }

                        {/* Tooltip fuera del Dropdown para evitar conflicto */}
                        <DropdownMenu>
                            <Tooltip>
                                <TooltipTrigger asChild>
                                    <DropdownMenuTrigger asChild>
                                        <Button
                                            variant="ghost"
                                            size="icon"
                                            className="shrink-0 "
                                        >
                                            <User className="h-5 w-5" />
                                            <span className="sr-only">Menú de usuario</span>
                                        </Button>
                                    </DropdownMenuTrigger>
                                </TooltipTrigger>

                                <TooltipContent>
                                    <p>Menú de usuario</p>
                                </TooltipContent>
                            </Tooltip>


                            <DropdownMenuContent align="end" className="w-56 z-9999">
                                {user ?
                                    <>
                                        <div className="px-2 py-1.5">
                                            <p className="text-sm font-medium">{user?.firstName}</p>
                                            <p className="text-xs text-muted-foreground">{user?.email}</p>
                                        </div>

                                        <DropdownMenuSeparator />

                                        {(user?.role?.code === RoleCode.ADMIN || user?.role?.code === RoleCode.STAFF) && (
                                            <>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/dashboard">
                                                        <Home className="mr-2 h-4 w-4" />
                                                        Ver Eventos Públicos
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                            </>
                                        )}

                                        {user?.role?.code === RoleCode.ADMIN && (
                                            <>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/admin">
                                                        <LayoutDashboard className="mr-2 h-4 w-4" />
                                                        Panel Admin
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/admin/events">
                                                        <Calendar className="mr-2 h-4 w-4" />
                                                        Mis Eventos
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                            </>
                                        )}

                                        {user?.role?.code === RoleCode.STAFF && (
                                            <>
                                                <DropdownMenuItem asChild>
                                                    <Link href="/staff">
                                                        <LayoutDashboard className="mr-2 h-4 w-4" />
                                                        Panel Staff
                                                    </Link>
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                            </>
                                        )}

                                        <DropdownMenuItem asChild>
                                            <Link href="/profile">
                                                <Settings className="mr-2 h-4 w-4" />
                                                Configuración
                                            </Link>
                                        </DropdownMenuItem>

                                        <DropdownMenuItem
                                            onClick={() => logout}
                                            className="text-destructive"
                                        >
                                            <LogOut className="mr-2 h-4 w-4" />
                                            Cerrar Sesión
                                        </DropdownMenuItem>
                                    </>
                                    :
                                    <DropdownMenuItem
                                        onClick={() => router.push("/login")}
                                        className="text-info"
                                    >
                                        <LogIn className="mr-2 h-4 w-4" />
                                        Iniciar Sesión
                                    </DropdownMenuItem>
                                }

                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </div>
            </div>
        </nav>
    )
}
