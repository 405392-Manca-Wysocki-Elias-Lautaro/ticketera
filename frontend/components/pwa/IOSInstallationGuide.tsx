"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover"
import { toast } from "sonner"
import { Info, Share } from "lucide-react"

export function IOSInstallGuide() {
    const [open, setOpen] = useState(false)

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    className="gradient-brand fixed bottom-4 right-4 z-50 opacity-90 shadow-md"
                    onClick={() => setOpen(true)}
                >
                    📱 Instalar aplicación
                </Button>
            </PopoverTrigger>

            <PopoverContent
                side="top"
                align="end"
                className="w-[300px] p-4 rounded-2xl shadow-lg border border-border bg-slate-900 text-gray-50"
            >
                <div className="space-y-3 text-center">
                    <div className="flex justify-center items-center gap-2">
                        <Info className="w-4 h-4 text-orange-500" />
                        <h3 className="text-sm font-semibold">Agregar a pantalla de inicio</h3>
                    </div>

                    <p className="text-xs text-muted-white leading-snug">
                        1️⃣ Tocá el ícono <b>Compartir</b>{" "}
                        <Share className="inline w-4 h-4 mx-1 text-blue-500" />
                        <br />
                        2️⃣ Elegí <b>“Agregar a pantalla de inicio”</b>
                        <br />
                        3️⃣ Confirmá haciendo click en <b>Agregar</b>
                    </p>

                    <Button
                        size="sm"
                        className="w-full text-muted-white"
                        onClick={() => {
                            setOpen(false)
                            toast.info("Seguí las instrucciones para completar la instalación")
                        }}
                    >
                        Entendido
                    </Button>
                </div>
            </PopoverContent>
        </Popover>
    )
}
