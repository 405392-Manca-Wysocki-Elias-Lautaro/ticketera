"use client"

import { useEffect, useState, useMemo } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Navbar } from "@/components/Navbar"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useAuth } from '@/hooks/auth/useAuth'
import { EventCard } from '@/components/EventCard'
import { mockEvents } from '@/mocks/mockEvents'

//TODO: Borrar
const categories = ["Todos", "Música", "Comedia", "Tecnología", "Deportes", "Teatro"]

export default function DashboardPage() {
    const router = useRouter()
    const searchParams = useSearchParams()
    const { user, isLoading } = useAuth()
    const [selectedCategory, setSelectedCategory] = useState("Todos")

    const searchQuery = useMemo(() => {
        return searchParams.get("search")?.toLowerCase() || ""
    }, [searchParams])

    const filteredEvents = useMemo(() => {
        let filtered = mockEvents

        // Filter by category
        if (selectedCategory !== "Todos") {
            filtered = filtered.filter((event) => event.category === selectedCategory)
        }

        // Filter by search query
        if (searchQuery) {
            filtered = filtered.filter(
                (event) =>
                    event.title.toLowerCase().includes(searchQuery) ||
                    event.description.toLowerCase().includes(searchQuery) ||
                    event.location.toLowerCase().includes(searchQuery),
            )
        }

        return filtered
    }, [selectedCategory, searchQuery])

    useEffect(() => {
        if (!isLoading && !user) {
            router.push("/login")
        }
    }, [user, isLoading, router])

    if (isLoading || !user) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-background">
            <Navbar />

            <main className="container mx-auto px-4 py-8">
                {/* Header */}
                <div className="mb-8">
                    <h1 className="text-3xl md:text-4xl font-bold mb-2 gradient-text">Descubre Eventos</h1>
                    <p className="text-muted-foreground">Encuentra y compra entradas para los mejores eventos</p>
                </div>

                {/* Category Filters */}
                <div className="mb-8 overflow-x-auto pb-2">
                    <Tabs value={selectedCategory} onValueChange={setSelectedCategory} className="w-full">
                        <TabsList className="inline-flex w-auto">
                            {categories.map((category) => (
                                <TabsTrigger key={category} value={category} className="whitespace-nowrap">
                                    {category}
                                </TabsTrigger>
                            ))}
                        </TabsList>
                    </Tabs>
                </div>

                {/* Events Grid */}
                {filteredEvents.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {filteredEvents.map((event) => (
                            <EventCard key={event.id} event={event} />
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-12">
                        <p className="text-muted-foreground text-lg">No se encontraron eventos para esta búsqueda</p>
                    </div>
                )}
            </main>
        </div>
    )
}
