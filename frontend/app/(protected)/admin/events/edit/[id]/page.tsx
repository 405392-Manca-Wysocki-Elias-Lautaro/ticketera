"use client"

import { useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
import { format } from "date-fns"
import { useForm, useFieldArray } from "react-hook-form"
import { toast } from "sonner"

import { useAuth } from "@/hooks/auth/useAuth"
import { useEvent } from "@/hooks/event/useEvent"
import { useCategories } from "@/hooks/event/useCategories"

import { RoleUtils } from "@/utils/roleUtils"
import type { CreateSeat } from "@/types/Request/CreateEvent"

import FullPageLoader from '@/components/admin/eventForm/FullPageLoader'
import EventPageHeader from '@/components/admin/eventForm/EventPageHeader'
import { EventBasicInfoSection } from '@/components/admin/eventForm/EventBasicInfoSection'
import { EventDateLocationSection } from '@/components/admin/eventForm/EventDateLocationSection'
import { EventAreasSection } from '@/components/admin/eventForm/EventAreasSection'

import { Button } from "@/components/ui/button"
import StarBorder from "@/components/StarBorder"
import { Loader2 } from "lucide-react"
import useNumberInputBehavior from '@/hooks/useNumberInputBehavior'

export default function EditEventPage() {
    const router = useRouter();
    const { id } = useParams();
    useNumberInputBehavior();

    const { user, isLoading: isLoadingAuth } = useAuth()
    const { data: event, isLoading: isLoadingEvent } = useEvent(id)
    const { data: categories, isLoading: isLoadingCategories } = useCategories()

    const {
        control,
        handleSubmit,
        register,
        watch,
        setValue,
        reset,
        formState: { isSubmitting },
    } = useForm<any>({
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

    // Restricción
    useEffect(() => {
        if (!isLoadingAuth && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoadingAuth, router])

    // Cargar datos del evento
    useEffect(() => {
        if (!event) return;

        const start = new Date(event.startsAt);
        const end = new Date(event.endsAt);

        const areasWithParsedPrices = event.areas.map((area: any) => ({
            ...area,
            priceCents: (area.priceCents ?? 0) / 100,
        }));

        reset({
            ...event,
            startDate: start,
            startTime: format(start, "HH:mm"),
            endDate: end,
            endTime: format(end, "HH:mm"),
            areas: areasWithParsedPrices
        });

        setTimeout(() => {
            setValue("areas", areasWithParsedPrices);
        });
    }, [event, reset, setValue]);

    const onSubmit = async (data: any) => {
        const startsAt = new Date(
            `${format(data.startDate, "yyyy-MM-dd")}T${data.startTime}`
        ).toISOString()

        const endsAt = data.endDate
            ? new Date(
                `${format(data.endDate, "yyyy-MM-dd")}T${data.endTime}`
            ).toISOString()
            : startsAt

        const payload = {
            ...data,
            startsAt,
            endsAt,
            areas: data.areas.map((area: any, index: number) => {
                if (area.isGeneralAdmission) {
                    return {
                        ...area,
                        capacity: Number(area.capacity),
                        position: index + 1,
                        priceCents: Math.round(Number(area.priceCents) * 100),
                        seats: []
                    }
                }

                const seats: CreateSeat[] = []

                area.rows.forEach((row: any, rowIndex: number) => {
                    for (let seat = row.startSeat; seat <= row.endSeat; seat++) {
                        seats.push({
                            seatNumber: seat,
                            rowNumber: rowIndex + 1,
                            label: `${row.name}-${seat}`
                        })
                    }
                })

                return {
                    ...area,
                    capacity: Number(area.capacity),
                    position: index + 1,
                    priceCents: Math.round(Number(area.priceCents) * 100),
                    seats
                }
            })
        }

        console.log("EDIT PAYLOAD:", payload)

        toast.success("Evento actualizado")
        router.push("/admin/events")
    }

    if (isLoadingAuth || isLoadingCategories || isLoadingEvent || !event) {
        return <FullPageLoader />
    }

    return (
        <div className="flex h-screen overflow-auto">
            <div className="container mx-auto px-4 py-8 max-w-4xl">

                <EventPageHeader title="Editar Evento" />

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
                        <Button asChild variant="outline">
                            <a href="/admin/events">Cancelar</a>
                        </Button>

                        <StarBorder className="flex-1">
                            <Button
                                type="submit"
                                className="w-full gradient-brand text-white"
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? (
                                    <Loader2 className="h-4 w-4 animate-spin mr-2" />
                                ) : (
                                    "Guardar Cambios"
                                )}
                            </Button>
                        </StarBorder>
                    </div>

                </form>
            </div>
        </div>
    )
}
