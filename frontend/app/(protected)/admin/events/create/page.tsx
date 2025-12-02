"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { format } from "date-fns"
import { useForm, useFieldArray } from "react-hook-form"
import { toast } from "sonner"

import { useAuth } from "@/hooks/auth/useAuth"
import { useCreateEvent } from "@/hooks/event/useCreateEvent"
import { useCategories } from "@/hooks/event/useCategories"

import { RoleUtils } from "@/utils/roleUtils"
import type { CreateEvent, CreateSeat } from "@/types/Request/CreateEvent"

import { Button } from "@/components/ui/button"
import StarBorder from "@/components/StarBorder"
import { Loader2 } from "lucide-react"

import FullPageLoader from '@/components/admin/eventForm/FullPageLoader'
import EventPageHeader from '@/components/admin/eventForm/EventPageHeader'
import { EventBasicInfoSection } from '@/components/admin/eventForm/EventBasicInfoSection'
import { EventDateLocationSection } from '@/components/admin/eventForm/EventDateLocationSection'
import { EventAreasSection } from '@/components/admin/eventForm/EventAreasSection'
import useNumberInputBehavior from '@/hooks/useNumberInputBehavior'

export default function CreateEventPage() {
    const router = useRouter()
    const { user, isLoading: isLoadingAuth } = useAuth()
    const { data: categories, isLoading: isLoadingCategories } = useCategories()
    useNumberInputBehavior();

    const {
        control,
        handleSubmit,
        register,
        watch,
        setValue,
        formState: { isSubmitting },
    } = useForm<CreateEvent & {
        startDate: Date
        endDate?: Date
        startTime: string
        endTime?: string
        areas: any[]
    }>({
        defaultValues: {
            title: "",
            description: "",
            categoryId: "",
            coverUrl: "",
            venueName: "",
            venueDescription: "",
            addressLine: "",
            city: "",
            state: "",
            country: "",
            startDate: undefined,
            startTime: "",
            endDate: undefined,
            endTime: "",
            areas: []
        }
    })

    const { fields: areas, append, remove } = useFieldArray({
        control,
        name: "areas"
    })

    // ⛔ PROTEGER PÁGINA
    useEffect(() => {
        if (!isLoadingAuth && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoadingAuth, router])

    const { mutate, isPending } = useCreateEvent()

    const onSubmit = (data: any) => {
        if (!user) return;

        const startsAt = new Date(
            `${format(data.startDate, "yyyy-MM-dd")}T${data.startTime}`
        ).toISOString()

        const endsAt =
            data.endDate && data.endTime
                ? new Date(
                    `${format(data.endDate, "yyyy-MM-dd")}T${data.endTime}`
                ).toISOString()
                : startsAt

        const payload: CreateEvent = {
            organizerId: user.id,
            title: data.title,
            slug: `${data.title.toLowerCase().replace(/\s+/g, "-")}-${Date.now()}`,
            description: data.description,

            categoryId: data.categoryId,
            coverUrl: data.coverUrl,
            status: "published",

            venueName: data.venueName,
            venueDescription: data.venueDescription,
            addressLine: data.addressLine,
            city: data.city,
            state: data.state,
            country: data.country,

            startsAt,
            endsAt,

            areas: data.areas.map((area: any, index: number) => {
                if (area.isGeneralAdmission) {
                    return {
                        name: area.name,
                        isGeneralAdmission: true,
                        capacity: Number(area.capacity),
                        position: Number(index + 1),
                        priceCents: Math.round(Number(area.priceCents) * 100),
                        seats: [],
                    }
                }

                const seats: CreateSeat[] = [];

                area.rows.forEach((row: any, rowIndex: number) => {
                    for (let seat = row.startSeat; seat <= row.endSeat; seat++) {
                        seats.push({
                            seatNumber: seat,
                            rowNumber: rowIndex + 1,
                            label: `${row.name}-${seat}`,
                        })
                    }
                })

                return {
                    name: area.name,
                    isGeneralAdmission: false,
                    capacity: Number(area.capacity),
                    position: index + 1,
                    priceCents: Math.round(Number(area.priceCents) * 100),
                    seats
                }
            })
        }

        mutate(payload, {
            onSuccess: () => {
                toast.success("Evento creado exitosamente")
                router.push("/admin/events")
            }
        })
    }

    if (isLoadingAuth || !user || isLoadingCategories) {
        return <FullPageLoader />
    }

    return (
        <div className="flex h-screen overflow-auto">
            <div className="container mx-auto px-4 py-8 max-w-4xl">
                
                <EventPageHeader title="Crear Nuevo Evento" />

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                    
                    <EventBasicInfoSection
                        control={control}
                        register={register}
                        categories={categories}
                    />

                    <EventDateLocationSection
                        control={control}
                        register={register}
                        watch={watch}
                    />

                    <EventAreasSection
                        control={control}
                        register={register}
                        watch={watch}
                        append={append}
                        remove={remove}
                        setValue={setValue}
                        areas={areas}
                    />

                    {/* Footer */}
                    <div className="flex gap-4">
                        <Button type="button" variant="outline" asChild>
                            <Link href="/admin/events">
                                Cancelar
                            </Link>
                        </Button>

                        <StarBorder className="flex-1">
                            <Button
                                type="submit"
                                className="w-full gradient-brand text-white"
                                disabled={isSubmitting || isPending || areas.length === 0}
                            >
                                {(isSubmitting || isPending) ? (
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                ) : (
                                    "Crear Evento"
                                )}
                            </Button>
                        </StarBorder>
                    </div>
                </form>
            </div>
        </div>
    )
}
