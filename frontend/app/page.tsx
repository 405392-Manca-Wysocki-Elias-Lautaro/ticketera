"use client"


import { useRouter } from 'next/navigation';
import { useEffect } from 'react'

//? Esta página puede (debe) ser landing page
export default function HomePage() {

    const router = useRouter()

    useEffect(() => {
            router.push("/app/dashboard")
    }, []);
    
    return (
        <div className="flex min-h-screen items-center justify-center">
            <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
        </div>
    )
}
