"use client"

import { Loader2 } from "lucide-react"

export function SpinnerOverlay() {
    return (
        <div className="absolute inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
            <Loader2 className="h-10 w-10 animate-spin text-white" />
        </div>
    )
}
