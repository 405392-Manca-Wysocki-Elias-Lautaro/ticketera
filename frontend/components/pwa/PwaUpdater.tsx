"use client"

import { useEffect, useRef } from "react"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { CheckCircle2, Rocket, WifiOff } from "lucide-react"

export default function PWAUpdater() {
    const registrationRef = useRef<ServiceWorkerRegistration | null>(null)

    // 1) Detectar Service Worker registrado
    useEffect(() => {
        if ("serviceWorker" in navigator) {
            navigator.serviceWorker.ready.then((reg) => {
                registrationRef.current = reg
            })
        }
    }, [])

    // 2) Notificar cuando estamos offline
    useEffect(() => {
        const onOffline = () => {
            toast.success("App lista sin conexión", {
                id: "pwa-offline",
                icon: <WifiOff className="text-green-500" />,
            })
        }
        window.addEventListener("offline", onOffline)
        return () => window.removeEventListener("offline", onOffline)
    }, [])

    // 3) Chequeo de nueva versión cada 15s (o el tiempo que quieras)
    useEffect(() => {
        const interval = setInterval(async () => {
            const reg = registrationRef.current
            if (!reg) return

            await reg.update()

            if (reg.waiting) {
                toast.warning(
                    <div className="space-y-3">
                        <h1 className="font-semibold text-base">Nueva versión disponible</h1>
                        <p className="text-sm text-muted-foreground">
                            Hay una actualización lista. Tocá “Actualizar”.
                        </p>

                        <Button
                            onClick={() => {
                                reg.waiting?.postMessage({ type: "SKIP_WAITING" })
                                window.location.reload()
                            }}
                        >
                            Actualizar
                        </Button>
                    </div>,
                    {
                        id: "pwa-update",
                        icon: <Rocket className="text-emerald-500" />,
                        duration: Infinity,
                    }
                )
            }
        }, 15000)

        return () => clearInterval(interval)
    }, [])

    // 4) Detecta actualización completada
    useEffect(() => {
        if ("serviceWorker" in navigator) {
            navigator.serviceWorker.addEventListener("controllerchange", () => {
                toast.success("Actualización completada", {
                    icon: <CheckCircle2 className="text-green-500" />,
                })
            })
        }
    }, [])

    return null
}
