"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { format } from "date-fns"
import { es } from "date-fns/locale"
import { useForm, Controller, useFieldArray } from "react-hook-form"
import { cn } from "@/lib/utils"

import { useAuth } from "@/hooks/auth/useAuth"
import { useCreateEvent } from "@/hooks/event/useCreateEvent"

import GradientText from "@/components/GradientText"
import StarBorder from "@/components/StarBorder"

import { RoleUtils } from "@/utils/roleUtils"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip"

import { ArrowLeft, Plus, Trash2, CalendarIcon, Loader2 } from "lucide-react"

import type { CreateEvent, CreateSeat } from "@/types/Request/CreateEvent"
import { useCategories } from '@/hooks/event/useCategories'
import { Category } from '@/types/Category'

export default function CreateEventPage() {
    const router = useRouter();
    const { user, isLoading: isLoadingAuth } = useAuth();

    const { data: categories, isLoading: isLoadingCategories } = useCategories();

    const {
        control,
        handleSubmit,
        register,
        watch,
        formState: { isSubmitting },
    } = useForm<CreateEvent & {
        startDate: Date
        endDate?: Date
        startTime: string
        endTime?: string

        // CAMPOS QUE NO VAN AL PAYLOAD PERO NECESITAMOS PARA LA UI
        areas: Array<{
            id: string
            name: string
            isGeneralAdmission: boolean
            capacity: number
            priceCents: number
            position: number
            rows: Array<{
                id: string
                name: string
                startSeat: number
                endSeat: number
            }>
        }>
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

            areas: [],
        }
    });

    const { fields: areas, append, remove, update } = useFieldArray({
        control,
        name: "areas",
    });

    useEffect(() => {
        if (!isLoadingAuth && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/app/dashboard")
        }
    }, [user, isLoadingAuth, router]);

    const handleAddArea = () => {
        append({
            id: crypto.randomUUID(),
            name: "",
            isGeneralAdmission: true,
            priceCents: 0,
            capacity: 0,
            position: areas.length + 1,
            rows: [],
        })
    };

    const handleRemoveArea = (index: number) => remove(index);

    const handleAddRow = (areaIndex: number) => {
        const area = areas[areaIndex]
        update(areaIndex, {
            ...area,
            rows: [
                ...area.rows,
                {
                    id: crypto.randomUUID(),
                    name: "",
                    startSeat: 1,
                    endSeat: 10
                },
            ],
        })
    };

    const handleRemoveRow = (areaIndex: number, rowId: string) => {
        const area = areas[areaIndex]
        update(areaIndex, {
            ...area,
            rows: area.rows.filter(r => r.id !== rowId)
        })
    };

    const { mutate, isPending } = useCreateEvent();

    const onSubmit = (data: any) => {
        if (!user) return;

        // Fecha inicio → ISO
        const startsAt = new Date(
            `${format(data.startDate, "yyyy-MM-dd")}T${data.startTime}`
        ).toISOString();

        // Fecha fin → ISO
        const endsAt =
            data.endDate && data.endTime
                ? new Date(
                    `${format(data.endDate, "yyyy-MM-dd")}T${data.endTime}`
                ).toISOString()
                : startsAt;

        // Construir payload EXACTO
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
                        position: Number(area.position),
                        priceCents: Number(area.priceCents),
                        seats: [],
                    };
                }

                const seats: CreateSeat[] = [];

                area.rows.forEach((row: any, rowIndex: number) => {
                    for (let seat = row.startSeat; seat <= row.endSeat; seat++) {
                        seats.push({
                            seatNumber: seat,
                            rowNumber: rowIndex + 1,
                            label: `${row.name}-${seat}`,
                        });
                    }
                });

                return {
                    name: area.name,
                    isGeneralAdmission: false,
                    capacity: Number(area.capacity),
                    position: Number(area.position),
                    priceCents: Number(area.priceCents),
                    seats,
                };
            }),
        };

        console.log("FINAL PAYLOAD:", payload);

        mutate(payload, {
            onSuccess: () => {
                router.push("/app/admin/events");
            }
        });
    };

    if (isLoadingAuth || !user || isLoadingCategories || !RoleUtils.isAdmin(user)) {
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
                        <Button variant="ghost" asChild className="mb-6 cursor-pointer">
                            <Link href="/app/admin/events">
                                <ArrowLeft className="mr-2 h-4 w-4" />
                                Volver a eventos
                            </Link>
                        </Button>
                    </div>
                    <GradientText>
                        <h1 className="text-3xl font-bold mb-8">Crear Nuevo Evento</h1>
                    </GradientText>
                </div>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">

                    {/* === Información Básica === */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Información Básica</CardTitle>
                        </CardHeader>

                        <CardContent className="space-y-4">
                            <div>
                                <Label>Título del Evento</Label>
                                <Input {...register("title", { required: true })} placeholder="Festival de Rock 2025" />
                            </div>

                            <div>
                                <Label>Descripción</Label>
                                <Textarea {...register("description", { required: true })} rows={4} />
                            </div>

                            <div>
                                <Label>Categoría</Label>
                                <Controller
                                    control={control}
                                    name="categoryId"
                                    render={({ field }) => (
                                        <Select onValueChange={field.onChange} value={field.value}>
                                            <SelectTrigger><SelectValue placeholder="Selecciona una categoría" /></SelectTrigger>
                                            <SelectContent>
                                                {categories.map((category: Category) => {
                                                    <SelectItem value={category.id}>{category.name}</SelectItem>
                                                })}
                                            </SelectContent>
                                        </Select>
                                    )}
                                />
                            </div>

                            <div>
                                <Label>Imagen (URL)</Label>
                                <Input type="url" {...register("coverUrl")} placeholder="https://..." />
                            </div>
                        </CardContent>
                    </Card>

                    {/* === Fecha y Ubicación === */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Fecha y Ubicación</CardTitle>
                        </CardHeader>

                        <CardContent className="space-y-4">

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <Label>Fecha de Inicio</Label>
                                    <Controller
                                        control={control}
                                        name="startDate"
                                        render={({ field }) => (
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <Button variant="outline" className={cn("w-full justify-start", !field.value && "text-muted-foreground")}>
                                                        <CalendarIcon className="mr-2 h-4 w-4" />
                                                        {field.value ? format(field.value, "PPP", { locale: es }) : "Seleccionar"}
                                                    </Button>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar selected={field.value} onSelect={field.onChange} mode="single" />
                                                </PopoverContent>
                                            </Popover>
                                        )}
                                    />
                                </div>

                                <div>
                                    <Label>Hora de Inicio</Label>
                                    <Input type="time" {...register("startTime", { required: true })} />
                                </div>
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <Label>Fecha de Fin</Label>
                                    <Controller
                                        control={control}
                                        name="endDate"
                                        render={({ field }) => (
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <Button variant="outline" className={cn("w-full justify-start", !field.value && "text-muted-foreground")}>
                                                        <CalendarIcon className="mr-2 h-4 w-4" />
                                                        {field.value ? format(field.value, "PPP", { locale: es }) : "Seleccionar"}
                                                    </Button>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar selected={field.value} onSelect={field.onChange} mode="single" />
                                                </PopoverContent>
                                            </Popover>
                                        )}
                                    />
                                </div>

                                <div>
                                    <Label>Hora de Fin</Label>
                                    <Input type="time" {...register("endTime")} />
                                </div>
                            </div>

                            <div>
                                <Label>Nombre del Venue</Label>
                                <Input {...register("venueName", { required: true })} placeholder="Estadio Luna Park" />
                            </div>

                            <div>
                                <Label>Descripción del Venue</Label>
                                <Textarea {...register("venueDescription")} rows={3} placeholder="Lugar emblemático..." />
                            </div>

                            <div>
                                <Label>Dirección</Label>
                                <Input {...register("addressLine", { required: true })} placeholder="Av. Corrientes 465" />
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <Input {...register("city", { required: true })} placeholder="Ciudad" />
                                <Input {...register("state", { required: true })} placeholder="Provincia" />
                                <Input {...register("country", { required: true })} placeholder="País" />
                            </div>

                        </CardContent>
                    </Card>

                    {/* === Áreas === */}
                    <Card>
                        <CardHeader>
                            <div className="flex justify-between items-center">
                                <CardTitle>Áreas y Precios</CardTitle>
                                <Button type="button" variant="outline" size="sm" onClick={handleAddArea}>
                                    <Plus className="mr-2 h-4 w-4" /> Agregar Área
                                </Button>
                            </div>
                        </CardHeader>

                        <CardContent className="space-y-6">
                            {areas.length === 0 && (
                                <p className="text-center text-muted-foreground py-8">No hay áreas agregadas.</p>
                            )}

                            {areas.map((area, i) => (
                                <Card key={area.id} className="border-2">
                                    <CardContent className="pt-6 space-y-4">

                                        <div className="flex justify-between items-start">
                                            <h4 className="font-semibold">Área {i + 1}</h4>
                                            <Tooltip>
                                                <TooltipTrigger asChild>
                                                    <Button variant="ghost" size="icon" onClick={() => handleRemoveArea(i)}>
                                                        <Trash2 className="h-4 w-4 text-destructive" />
                                                    </Button>
                                                </TooltipTrigger>
                                                <TooltipContent>Eliminar área</TooltipContent>
                                            </Tooltip>
                                        </div>

                                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                            <Input {...register(`areas.${i}.name` as const, { required: true })} placeholder="Campo, Platea Alta..." />

                                            <Controller
                                                control={control}
                                                name={`areas.${i}.isGeneralAdmission` as const}
                                                render={({ field }) => (
                                                    <Select onValueChange={(v) => field.onChange(v === "true")} value={String(field.value)}>
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

                                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                            <Input type="number" {...register(`areas.${i}.priceCents` as const)} placeholder="Precio en centavos" />
                                            <Input type="number" {...register(`areas.${i}.capacity` as const)} placeholder="Capacidad" />
                                            <Input type="number" {...register(`areas.${i}.position` as const)} placeholder="Posición" />
                                        </div>

                                        {/* Filas solo si NO es general admission */}
                                        {!watch(`areas.${i}.isGeneralAdmission`) && (
                                            <div className="pt-4 border-t space-y-3">
                                                <div className="flex justify-between">
                                                    <Label>Filas (se convertirán en asientos)</Label>
                                                    <Button type="button" size="sm" variant="outline" onClick={() => handleAddRow(i)}>
                                                        <Plus className="mr-2 h-3 w-3" /> Agregar Fila
                                                    </Button>
                                                </div>

                                                {area.rows.map((row) => (
                                                    <div key={row.id} className="grid grid-cols-12 gap-2 items-end">
                                                        <Input
                                                            className="col-span-3"
                                                            placeholder="Fila A"
                                                            value={row.name}
                                                            onChange={(e) => {
                                                                const newRows = area.rows.map(r =>
                                                                    r.id === row.id ? { ...r, name: e.target.value } : r
                                                                )
                                                                update(i, { ...area, rows: newRows })
                                                            }}
                                                        />

                                                        <Input
                                                            className="col-span-4"
                                                            type="number"
                                                            placeholder="Inicio"
                                                            value={row.startSeat}
                                                            onChange={(e) => {
                                                                const newRows = area.rows.map(r =>
                                                                    r.id === row.id ? { ...r, startSeat: Number(e.target.value) } : r
                                                                )
                                                                update(i, { ...area, rows: newRows })
                                                            }}
                                                        />

                                                        <Input
                                                            className="col-span-4"
                                                            type="number"
                                                            placeholder="Fin"
                                                            value={row.endSeat}
                                                            onChange={(e) => {
                                                                const newRows = area.rows.map(r =>
                                                                    r.id === row.id ? { ...r, endSeat: Number(e.target.value) } : r
                                                                )
                                                                update(i, { ...area, rows: newRows })
                                                            }}
                                                        />

                                                        <Button variant="ghost" size="icon" onClick={() => handleRemoveRow(i, row.id)}>
                                                            <Trash2 className="h-4 w-4 text-destructive" />
                                                        </Button>
                                                    </div>
                                                ))}
                                            </div>
                                        )}

                                    </CardContent>
                                </Card>
                            ))}
                        </CardContent>
                    </Card>
                    {/* === Footer === */}
                    <div className="flex gap-4">
                        <Button type="button" variant="outline" size="lg" asChild>
                            <Link href="/app/admin/events">Cancelar</Link>
                        </Button>

                        <StarBorder className="flex-1">
                            <Button
                                type="submit"
                                size="lg"
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
