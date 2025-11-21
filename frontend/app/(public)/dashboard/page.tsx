"use client"

import { useState, useMemo } from "react"
import { useSearchParams } from "next/navigation"
import { Navbar } from "@/components/Navbar"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useAuth } from '@/hooks/auth/useAuth'
import GradientText from '@/components/GradientText'
import { EventCard } from '@/components/dashboard/EventCard'
import { useEvents } from '@/hooks/event/useEvents'
import { useCategories } from '@/hooks/event/useCategories'
import { Category } from '@/types/Category'
import { InstallPWAButton } from '@/components/pwa/InstallPwaButton'

export default function DashboardPage() {

    const searchParams = useSearchParams()
    const { isLoading: loadingAuth } = useAuth()
    const [selectedCategory, setSelectedCategory] = useState("Todos");

    const searchQuery = useMemo(() => {
        return searchParams.get("search")?.toLowerCase() || ""
    }, [searchParams])

    const { data: events, isLoading: isLoadingEvent } = useEvents();
    const { data: categories, isLoading: isLoadingCategories } = useCategories();

    const filteredEvents = useMemo(() => {
        return (events ?? []).filter((event: Event) => {
            const matchesCategory = selectedCategory === "Todos" || event.categoryName === selectedCategory;
            const matchesSearch = !searchQuery ||
                event.title.toLowerCase().includes(searchQuery) ||
                event.description.toLowerCase().includes(searchQuery) ||
                event.venueName.toLowerCase().includes(searchQuery);
            return matchesCategory && matchesSearch;
        });
    }, [events, selectedCategory, searchQuery]);

    if (loadingAuth || isLoadingEvent || isLoadingCategories || !categories) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="h-screen bg-background overflow-auto">
            <Navbar />

            <InstallPWAButton />

            <div className="h-auto w-full mx-auto px-6 py-8">
                {/* Header */}
                <div className="mb-8">
                    <GradientText>
                        <h1 className="text-3xl md:text-4xl font-bold mb-2">Descubre Eventos</h1>
                    </GradientText>
                    <p className="text-muted-foreground text-center">Encuentra y compra entradas para los mejores eventos</p>
                </div>

                {/* Category Filters */}
                <div className="mb-8 overflow-x-auto pb-2">
                    <Tabs value={selectedCategory} onValueChange={setSelectedCategory} className="w-full">
                        <TabsList className="flex w-max min-w-full space-x-1 overflow-x-auto md:inline-flex px-1 scrollbar-none">
                            <TabsTrigger key={0} value={"Todos"} className="whitespace-nowrap w-full px-3 py-1.5">
                                Todos
                            </TabsTrigger>
                            {categories.map((category: Category) => (
                                <TabsTrigger key={category.id} value={category.name} className="whitespace-nowrap w-full px-3 py-1.5">
                                    {category.name}
                                </TabsTrigger>
                            ))}
                        </TabsList>
                    </Tabs>
                </div>

                {/* Events Grid */}
                {filteredEvents?.length > 0 ? (
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
