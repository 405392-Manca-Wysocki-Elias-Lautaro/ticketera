"use client"

import { useEffect, useState, useMemo } from "react"
import { useRouter, useSearchParams } from "next/navigation"
import { Navbar } from "@/components/Navbar"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useAuth } from '@/hooks/auth/useAuth'
import { mockEvents } from '@/mocks/mockEvents'
import GradientText from '@/components/GradientText'
import { EventCard } from '@/components/dashboard/EventCard'

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

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="h-screen bg-background overflow-auto">
            <Navbar />

            <div className="h-auto w-full mx-auto px-6 py-8">
                {/* Header */}
                <div className="mb-8">
                    <GradientText>
                        <h1 className="text-3xl md:text-4xl font-bold mb-2">Descubre Eventos</h1>
                    </GradientText>
                    <p className="text-muted-foreground">Encuentra y compra entradas para los mejores eventos</p>
                </div>

                {/* Category Filters */}
                <div className="mb-8 overflow-x-auto pb-2">
                    <Tabs value={selectedCategory} onValueChange={setSelectedCategory} className="w-full">
                        <TabsList className="inline-flex w-full ">
                            {categories.map((category) => (
                                <TabsTrigger key={category} value={category} className="whitespace-nowrap w-full">
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
            </div>
        </div>
    )
}
