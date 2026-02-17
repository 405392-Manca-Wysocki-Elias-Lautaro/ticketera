"use client"

import { Card, CardContent } from "@/components/ui/card"
import { User, Ticket } from "lucide-react"
import Link from "next/link"


export const RoleSelectionStep = () => {
    return (
        <div className="flex flex-col gap-6 w-full max-w-2xl mx-auto items-center">
            <div className="text-center space-y-2">
                <h1 className="text-3xl font-bold text-white drop-shadow-sm">Bienvenido a Ticketly</h1>
                <p className="text-white/80 font-medium drop-shadow-sm">Selecciona cómo quieres usar la plataforma</p>
            </div>

            <div className="grid md:grid-cols-2 gap-4 w-full">
                {/* Opción Usuario */}
                <Link
                    href="/signup/customer"
                    className="h-full w-full rounded-xl cursor-pointer hover:scale-[1.02] transition-transform duration-300 block"
                >
                    <Card className="h-full bg-card/50 backdrop-blur-sm border-0 transition-all duration-300 hover:bg-card/80">
                        <CardContent className="p-6 flex flex-col items-center text-center gap-4">
                            <div className="p-4 rounded-full bg-primary/10 group-hover:bg-primary/20 transition-colors">
                                <User className="w-8 h-8 text-primary" />
                            </div>
                            <div className="space-y-2">
                                <h3 className="font-semibold text-xl">Soy Usuario</h3>
                                <p className="text-sm text-muted-foreground">
                                    Quiero descubrir eventos, comprar entradas y gestionar mis asistencias.
                                </p>
                            </div>
                        </CardContent>
                    </Card>
                </Link>

                {/* Opción Organizador */}
                <Link
                    href="/signup/organizer"
                    className="h-full w-full rounded-xl cursor-pointer hover:scale-[1.02] transition-transform duration-300 block"
                >
                    <Card className="h-full bg-card/50 backdrop-blur-sm border-0 transition-all duration-300 hover:bg-card/80">
                        <CardContent className="p-6 flex flex-col items-center text-center gap-4">
                            <div className="p-4 rounded-full bg-primary/10 group-hover:bg-primary/20 transition-colors">
                                <Ticket className="w-8 h-8 text-primary" />
                            </div>
                            <div className="space-y-2">
                                <h3 className="font-semibold text-xl">Soy Organizador</h3>
                                <p className="text-sm text-muted-foreground">
                                    Quiero crear eventos, vender entradas y gestionar mi organización.
                                </p>
                            </div>
                        </CardContent>
                    </Card>
                </Link>
            </div>
        </div>
    )
}
