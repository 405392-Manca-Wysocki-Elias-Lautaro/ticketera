"use client"

import { useEffect } from "react"

export default function useNumberInputBehavior() {
    useEffect(() => {
        // Seleccionar todo al hacer focus
        const handleFocus = (e: Event) => {
            const target = e.target as HTMLInputElement
            if (target.type === "number") {
                setTimeout(() => target.select(), 0)
            }
        }

        // Bloquear scroll para evitar cambiar valor
        const handleWheel = (e: WheelEvent) => {
            const target = e.target as HTMLInputElement
            if (target.type === "number" && document.activeElement === target) {
                e.preventDefault()
            }
        }

        document.addEventListener("focusin", handleFocus)
        document.addEventListener("wheel", handleWheel, { passive: false })

        return () => {
            document.removeEventListener("focusin", handleFocus)
            document.removeEventListener("wheel", handleWheel)
        }
    }, [])

    return null
}
