"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import StarBorder from "../StarBorder"
import { IOSInstallGuide } from "./IOSInstallationGuide"
import { toast } from 'sonner'

function getPlatform(): "android" | "ios" | "desktop" {
    if (process.env.NODE_ENV === "development") return "android";
    const ua = navigator.userAgent.toLowerCase();
    if (/android/i.test(ua)) return "android";
    if (/iphone|ipad|ipod/i.test(ua)) return "ios";
    return "desktop";
}

export function InstallPWAButton() {
    const [platform, setPlatform] = useState<"android" | "ios" | "desktop">("desktop")
    const [isStandalone, setIsStandalone] = useState(false)
    const [deferredPrompt, setDeferredPrompt] = useState<any>(null)

    useEffect(() => {
        setPlatform(getPlatform())

        const standalone =
            window.matchMedia("(display-mode: standalone)").matches ||
            (window.navigator as any).standalone
        setIsStandalone(standalone)

        // 👇 LEEMOS LO CAPTURADO en /pwa-install.js
        const prompt = (window as any)._deferredInstallPrompt
        if (prompt) {
            console.log("✔ deferredPrompt detectado desde script global")
            setDeferredPrompt(prompt)
        }

        // También escuchamos si lo capturan tarde
        const interval = setInterval(() => {
            const latePrompt = (window as any)._deferredInstallPrompt
            if (latePrompt && !deferredPrompt) {
                console.log("✔ deferredPrompt capturado tardíamente")
                setDeferredPrompt(latePrompt)
            }
        }, 500)

        return () => clearInterval(interval)
    }, [])

    // no mostramos nada
    if (platform === "desktop" || isStandalone) return null

    // ANDROID → botón real de instalación
    if (platform === "android" && deferredPrompt) {
        const install = async () => {
            deferredPrompt.prompt()
            const choice = await deferredPrompt.userChoice

            if (choice.outcome === "accepted") {
                toast.success("📲 Instalación aceptada")
                ;(window as any)._deferredInstallPrompt = null
                setDeferredPrompt(null)
            } else {
                toast.error("Instalación cancelada")
            }
        }

        return (
            <StarBorder className="z-999">
                <Button
                    onClick={install}
                    className="fixed gradient-brand bottom-4 right-4 shadow-lg"
                >
                    📲 Instalar aplicación
                </Button>
            </StarBorder>
        )
    }

    // iOS → guía manual
    if (platform === "ios") {
        return <IOSInstallGuide />
    }

    return null
}
