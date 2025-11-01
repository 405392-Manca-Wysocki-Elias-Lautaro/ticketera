"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from '@/hooks/auth/useAuth'

//? Esta página puede (debe) ser landing page
export default function HomePage() {
    return (
        <div className="flex min-h-screen items-center justify-center">
            <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
        </div>
    )
}
