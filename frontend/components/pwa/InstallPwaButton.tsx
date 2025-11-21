"use client"

import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import StarBorder from "../StarBorder"
import { IOSInstallGuide } from "./IOSInstallationGuide"

function getPlatform(): "android" | "ios" | "desktop" {
    if (process.env.NODE_ENV == "development") return 'android';
    const ua = navigator.userAgent.toLowerCase();
    if (/android/i.test(ua)) return "android";
    if (/iphone|ipad|ipod/i.test(ua)) return "ios";
    return "desktop";
}

export function InstallPWAButton() {
    const [platform, setPlatform] = useState<"android" | "ios" | "desktop">("desktop")
    const [isStandalone, setIsStandalone] = useState(false)

    useEffect(() => {
        const p = getPlatform()
        setPlatform(p)

        const standalone =
            window.matchMedia("(display-mode: standalone)").matches ||
            (window.navigator as any).standalone

        setIsStandalone(standalone)
    }, [])

    //  desktop => NO mostramos nada
    if (platform === "desktop") return null

    //  ya instalada => oculto
    if (isStandalone) return null

    //  ANDROID → usar fallback: mostrar botón que explique cómo instalar
    if (platform === "android") {
        return (
            <StarBorder>
                <Button
                    onClick={() => {
                        alert("Para instalar: Abrí el menú ⋮ de Chrome > 'Agregar a pantalla principal'")
                    }}
                    className="fixed gradient-brand bottom-4 right-4 z-50 shadow-lg"
                >
                    📲 Instalar aplicación
                </Button>
            </StarBorder>
        )
    }

    //  iOS → mostrar guía especial
    if (platform === "ios") {
        return <IOSInstallGuide />
    }

    return null
}
