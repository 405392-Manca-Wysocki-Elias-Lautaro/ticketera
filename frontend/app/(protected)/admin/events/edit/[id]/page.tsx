"use client"

import { useEffect } from "react"
import { useRouter, useParams } from "next/navigation"
import Link from "next/link"
import { format } from "date-fns"
import { es } from "date-fns/locale"
import { useForm, Controller, useFieldArray } from "react-hook-form"
import { cn } from "@/lib/utils"

import { useAuth } from "@/hooks/auth/useAuth"
import { RoleUtils } from "@/utils/roleUtils"

import GradientText from "@/components/GradientText"
import StarBorder from "@/components/StarBorder"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip"

import { ArrowLeft, Plus, Trash2, CalendarIcon, Loader2 } from "lucide-react"

import type { CreateEvent, CreateSeat } from "@/types/Request/CreateEvent"
import { useCategories } from "@/hooks/event/useCategories"
import { toast } from "sonner"
import { useEvent } from '@/hooks/event/useEvent'

export default function EditEventPage() {
    const router = useRouter();
    const { id } = useParams();
    const { user, isLoading: isLoadingAuth } = useAuth();

    const { data: event, isLoading: isLoadingEvent } = useEvent(id);
    const { data: categories, isLoading: isLoadingCategories } = useCategories()

    const {
        control,
        handleSubmit,
        register,
        watch,
        setValue,
        reset,
        formState: { isSubmitting }
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

    // 🟦 PROTEGER PÁGINA
    useEffect(() => {
        if (!isLoadingAuth && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoadingAuth, router]);

    // 🟦 CARGAR EVENTO
    useEffect(() => {
        async function load() {

            if (!event) return;

            const start = new Date(event.startsAt)
            const end = new Date(event.endsAt)

            reset({
                ...event,
                startDate: start,
                startTime: format(start, "HH:mm"),
                endDate: end,
                endTime: format(end, "HH:mm"),
                areas: event.areas
            })

            setTimeout(() => {
                setValue("areas", event.areas || []);
            });
        }

        load()
    }, [id, reset, event])

    const onSubmit = async (data: any) => {
        const startsAt = new Date(
            `${format(data.startDate, "yyyy-MM-dd")}T${data.startTime}`
        ).toISOString()

        const endsAt = data.endDate
            ? new Date(`${format(data.endDate, "yyyy-MM-dd")}T${data.endTime}`).toISOString()
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
                        priceCents: Number(area.priceCents),
                        seats: []
                    }
                }

                // Generar asientos
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
                    priceCents: Number(area.priceCents),
                    seats
                }
            })
        }

        console.log("EDIT PAYLOAD:", payload)

        toast.success("Evento actualizado")
        router.push("/admin/events")
    }

    if (isLoadingAuth || isLoadingCategories || isLoadingEvent || !event) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        )
    }

    return (
        <div className="flex h-screen overflow-auto">
            <div className="container mx-auto px-4 py-8 max-w-4xl">
                <div className="relative flex items-center justify-center mb-8">
                    <div className="absolute left-0">
                        <Button variant="ghost" asChild>
                            <Link href="/admin/events">
                                <ArrowLeft className="mr-2 h-4 w-4" />
                                Volver a eventos
                            </Link>
                        </Button>
                    </div>

                    <GradientText>
                        <h1 className="text-3xl font-bold mb-8">Editar Evento</h1>
                    </GradientText>
                </div>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                    {/* === Información Básica === */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Información Básica</CardTitle>
                        </CardHeader>

                        <CardContent className="space-y-4">
                            <Input {...register("title", { required: true })} placeholder="Título" />
                            <Textarea {...register("description", { required: true })} rows={4} />

                            <Controller
                                control={control}
                                name="categoryId"
                                render={({ field }) => (
                                    <Select onValueChange={field.onChange} value={field.value}>
                                        <SelectTrigger>
                                            <SelectValue placeholder="Categoría" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {categories?.map((c: any) => (
                                                <SelectItem key={c.id} value={c.id}>
                                                    {c.name}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                )}
                            />

                            <Input {...register("coverUrl")} placeholder="URL de imagen" />
                        </CardContent>
                    </Card>

                    {/* === Fecha === */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Fecha y Ubicación</CardTitle>
                        </CardHeader>

                        <CardContent className="space-y-4">
                            {/* START */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <Controller
                                    control={control}
                                    name="startDate"
                                    render={({ field }) => (
                                        <Popover>
                                            <PopoverTrigger asChild>
                                                <Button variant="outline" className="justify-start w-full">
                                                    <CalendarIcon className="mr-2 h-4 w-4" />
                                                    {field.value
                                                        ? format(field.value, "PPP", { locale: es })
                                                        : "Fecha inicio"}
                                                </Button>
                                            </PopoverTrigger>
                                            <PopoverContent>
                                                <Calendar selected={field.value} onSelect={field.onChange} />
                                            </PopoverContent>
                                        </Popover>
                                    )}
                                />

                                <Input type="time" {...register("startTime", { required: true })} />
                            </div>

                            {/* END */}
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <Controller
                                    control={control}
                                    name="endDate"
                                    render={({ field }) => (
                                        <Popover>
                                            <PopoverTrigger asChild>
                                                <Button variant="outline" className="justify-start w-full">
                                                    <CalendarIcon className="mr-2 h-4 w-4" />
                                                    {field.value
                                                        ? format(field.value, "PPP", { locale: es })
                                                        : "Fecha fin"}
                                                </Button>
                                            </PopoverTrigger>
                                            <PopoverContent>
                                                <Calendar selected={field.value} onSelect={field.onChange} />
                                            </PopoverContent>
                                        </Popover>
                                    )}
                                />

                                <Input type="time" {...register("endTime")} />
                            </div>

                            {/* Ubicación */}
                            <Input {...register("venueName", { required: true })} placeholder="Lugar" />
                            <Textarea {...register("venueDescription")} placeholder="Descripción lugar" />
                            <Input {...register("addressLine")} placeholder="Dirección" />
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <Input {...register("city")} placeholder="Ciudad" />
                                <Input {...register("state")} placeholder="Provincia" />
                                <Input {...register("country")} placeholder="País" />
                            </div>
                        </CardContent>
                    </Card>

                    {/* === Áreas === */}
                    <Card>
                        <CardHeader>
                            <div className="flex justify-between items-center">
                                <CardTitle>Áreas y Precios</CardTitle>
                                <Button
                                    type="button"
                                    onClick={() => append({
                                        id: crypto.randomUUID(),
                                        name: "",
                                        isGeneralAdmission: true,
                                        capacity: 0,
                                        priceCents: 0,
                                        position: areas.length + 1,
                                        rows: []
                                    })}
                                >
                                    <Plus className="mr-2 h-4 w-4" /> Agregar Área
                                </Button>
                            </div>
                        </CardHeader>

                        <CardContent className="space-y-6">
                            {(areas ?? []).map((area, i) => {
                                const rows = watch(`areas.${i}.rows`) ?? [];

                                return (
                                    <Card key={area.id} className="border">
                                        <CardContent className="space-y-4 pt-6">

                                            <div className="flex justify-between">
                                                <h4 className="font-semibold">Área {i + 1}</h4>

                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => remove(i)}
                                                >
                                                    <Trash2 className="h-4 w-4 text-destructive" />
                                                </Button>
                                            </div>

                                            {/* nombre + tipo */}
                                            <div className="grid grid-cols-2 gap-4">
                                                <Input {...register(`areas.${i}.name`)} placeholder="Nombre" />

                                                <Controller
                                                    control={control}
                                                    name={`areas.${i}.isGeneralAdmission`}
                                                    render={({ field }) => (
                                                        <Select
                                                            onValueChange={(v) => {
                                                                const val = v === "true"
                                                                field.onChange(val)

                                                                if (!val && rows.length === 0) {
                                                                    setValue(`areas.${i}.rows`, [
                                                                        {
                                                                            id: crypto.randomUUID(),
                                                                            name: "Fila 1",
                                                                            startSeat: 1,
                                                                            endSeat: 10
                                                                        }
                                                                    ])
                                                                }

                                                                if (val) {
                                                                    setValue(`areas.${i}.rows`, [])
                                                                }
                                                            }}
                                                            value={String(field.value)}
                                                        >
                                                            <SelectTrigger>
                                                                <SelectValue placeholder="Tipo" />
                                                            </SelectTrigger>

                                                            <SelectContent>
                                                                <SelectItem value="true">General</SelectItem>
                                                                <SelectItem value="false">Numerada</SelectItem>
                                                            </SelectContent>
                                                        </Select>
                                                    )}
                                                />
                                            </div>

                                            {/* precio + capacidad */}
                                            <div className="grid grid-cols-2 gap-4">
                                                <Input
                                                    type="number"
                                                    {...register(`areas.${i}.priceCents`)}
                                                    placeholder="Precio"
                                                />

                                                {watch(`areas.${i}.isGeneralAdmission`) && (
                                                    <Input
                                                        type="number"
                                                        {...register(`areas.${i}.capacity`)}
                                                        placeholder="Capacidad"
                                                    />
                                                )}
                                            </div>

                                            {/* filas */}
                                            {!watch(`areas.${i}.isGeneralAdmission`) && (
                                                <div className="space-y-4 mt-4">
                                                    <Button
                                                        type="button"
                                                        size="sm"
                                                        onClick={() =>
                                                            setValue(`areas.${i}.rows`, [
                                                                ...rows,
                                                                {
                                                                    id: crypto.randomUUID(),
                                                                    name: `Fila ${rows.length + 1}`,
                                                                    startSeat: 1,
                                                                    endSeat: 10
                                                                }
                                                            ])
                                                        }
                                                    >
                                                        <Plus className="mr-2 h-4 w-4" /> Agregar fila
                                                    </Button>

                                                    {(rows ?? []).map((row, rowIndex) => (

                                                        <div key={row.id} className="grid grid-cols-12 gap-2 items-center">

                                                            <Input
                                                                {...register(`areas.${i}.rows.${rowIndex}.name`)}
                                                                className="col-span-3"
                                                                placeholder="Fila"
                                                            />

                                                            <Input
                                                                type="number"
                                                                {...register(`areas.${i}.rows.${rowIndex}.startSeat`)}
                                                                className="col-span-4"
                                                            />

                                                            <Input
                                                                type="number"
                                                                {...register(`areas.${i}.rows.${rowIndex}.endSeat`)}
                                                                className="col-span-4"
                                                            />

                                                            <Button
                                                                type="button"
                                                                variant="ghost"
                                                                size="icon"
                                                                onClick={() => {
                                                                    const newRows = rows.filter(r => r.id !== row.id)
                                                                    setValue(`areas.${i}.rows`, newRows)
                                                                }}
                                                            >
                                                                <Trash2 className="h-4 w-4 text-destructive" />
                                                            </Button>

                                                        </div>
                                                    ))}
                                                </div>
                                            )}
                                        </CardContent>
                                    </Card>
                                )
                            })}
                        </CardContent>
                    </Card>

                    {/* === FOOTER === */}
                    <div className="flex gap-4">
                        <Button asChild variant="outline">
                            <Link href="/admin/events">Cancelar</Link>
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
