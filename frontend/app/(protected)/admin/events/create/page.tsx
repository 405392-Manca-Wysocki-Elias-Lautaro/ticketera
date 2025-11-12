"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { format } from "date-fns"
import { es } from "date-fns/locale"
import { useForm, Controller, useFieldArray } from "react-hook-form"
import { cn } from "@/lib/utils"
import { useAuth } from "@/hooks/auth/useAuth"
import GradientText from "@/components/GradientText"
import { RoleUtils } from "@/utils/roleUtils"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Switch } from "@/components/ui/switch"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Calendar } from "@/components/ui/calendar"
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip"
import { ArrowLeft, Plus, Trash2, CalendarIcon, Clock, Upload, LinkIcon, MapPin, Loader2 } from "lucide-react"
import StarBorder from "@/components/StarBorder"

interface RowForm {
    id: string
    name: string
    startSeat: number
    endSeat: number
}

interface AreaForm {
    id: string
    name: string
    type: "general" | "numbered"
    price: number
    capacity: number
    rows: RowForm[]
}

interface EventForm {
    title: string
    description: string
    category: string
    imageType: "url" | "upload"
    imageUrl?: string
    imageFile?: File | null
    startDate: Date
    endDate?: Date
    startTime: string
    endTime?: string
    location: string
    address: string
    useGoogleMaps: boolean
    mapLocation?: string
    areas: AreaForm[]
}

export default function CreateEventPage() {
    const router = useRouter()
    const { user, isLoading } = useAuth()

    const {
        control,
        handleSubmit,
        register,
        watch,
        formState: { isSubmitting },
    } = useForm<EventForm>({
        defaultValues: {
            imageType: "url",
            useGoogleMaps: false,
            areas: [],
        },
    })

    const { fields: areas, append, remove, update } = useFieldArray({
        control,
        name: "areas",
    })

    // redirect si no es admin
    useEffect(() => {
        if (!isLoading && (!user || !RoleUtils.isAdmin(user))) {
            router.push("/dashboard")
        }
    }, [user, isLoading, router])

    const handleAddArea = () => {
        append({
            id: crypto.randomUUID(),
            name: "",
            type: "general",
            price: 0,
            capacity: 0,
            rows: [],
        })
    }

    const handleRemoveArea = (index: number) => remove(index)

    const handleAddRow = (areaIndex: number) => {
        const area = areas[areaIndex]
        update(areaIndex, {
            ...area,
            rows: [
                ...(area.rows || []),
                { id: crypto.randomUUID(), name: "", startSeat: 1, endSeat: 10 },
            ],
        })
    }

    const handleRemoveRow = (areaIndex: number, rowId: string) => {
        const area = areas[areaIndex]
        update(areaIndex, {
            ...area,
            rows: area.rows.filter((r) => r.id !== rowId),
        })
    }

    const onSubmit = async (data: EventForm) => {
        console.log("✅ Form data:", data)
        await new Promise((r) => setTimeout(r, 1500))
        router.push("/admin/events")
    }

    if (isLoading || !user || !RoleUtils.isAdmin(user)) {
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
                            <Link href="/admin/events">
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
                                <Label>Nombre del Evento</Label>
                                <Input {...register("title", { required: true })} placeholder="Festival de Rock 2025" />
                            </div>

                            <div>
                                <Label>Descripción</Label>
                                <Textarea {...register("description", { required: true })} placeholder="Describe tu evento..." rows={4} />
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                {/* Categoría */}
                                <div>
                                    <Label>Categoría</Label>
                                    <Controller
                                        control={control}
                                        name="category"
                                        render={({ field }) => (
                                            <Select onValueChange={field.onChange} value={field.value}>
                                                <SelectTrigger><SelectValue placeholder="Selecciona una categoría" /></SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="musica">Música</SelectItem>
                                                    <SelectItem value="comedia">Comedia</SelectItem>
                                                    <SelectItem value="tecnologia">Tecnología</SelectItem>
                                                    <SelectItem value="deportes">Deportes</SelectItem>
                                                    <SelectItem value="teatro">Teatro</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        )}
                                    />
                                </div>

                                {/* Imagen */}
                                <div>
                                    <Label>Imagen del Evento</Label>
                                    <Controller
                                        control={control}
                                        name="imageType"
                                        render={({ field }) => (
                                            <Tabs value={field.value} onValueChange={field.onChange}>
                                                <TabsList className="grid w-full grid-cols-2">
                                                    <TabsTrigger value="url"><LinkIcon className="mr-2 h-4 w-4" />URL</TabsTrigger>
                                                    <TabsTrigger value="upload"><Upload className="mr-2 h-4 w-4" />Subir</TabsTrigger>
                                                </TabsList>
                                                <TabsContent value="url">
                                                    <Input type="url" {...register("imageUrl")} placeholder="https://..." />
                                                </TabsContent>
                                                <TabsContent value="upload">
                                                    <Input type="file" accept="image/*" {...register("imageFile")} />
                                                </TabsContent>
                                            </Tabs>
                                        )}
                                    />
                                </div>
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
                                                    <Button variant="outline" className={cn("w-full justify-start text-left font-normal", !field.value && "text-muted-foreground")}>
                                                        <CalendarIcon className="mr-2 h-4 w-4" />
                                                        {field.value ? format(field.value, "PPP", { locale: es }) : "Selecciona una fecha"}
                                                    </Button>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar mode="single" selected={field.value} onSelect={field.onChange} />
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
                                    <Label>Fecha de Fin (opcional)</Label>
                                    <Controller
                                        control={control}
                                        name="endDate"
                                        render={({ field }) => (
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <Button variant="outline" className={cn("w-full justify-start text-left font-normal", !field.value && "text-muted-foreground")}>
                                                        <CalendarIcon className="mr-2 h-4 w-4" />
                                                        {field.value ? format(field.value, "PPP", { locale: es }) : "Selecciona una fecha"}
                                                    </Button>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar mode="single" selected={field.value} onSelect={field.onChange} />
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
                                <Label>Lugar</Label>
                                <Input {...register("location", { required: true })} placeholder="Estadio Nacional" />
                            </div>

                            <div>
                                <Label>Dirección Completa</Label>
                                <Input {...register("address", { required: true })} placeholder="Av. Principal 1234, Ciudad" />
                            </div>

                            <div className="flex items-center justify-between p-4 border rounded-lg">
                                <div>
                                    <Label>Usar Google Maps</Label>
                                    <p className="text-sm text-muted-foreground">Permite seleccionar ubicación en el mapa</p>
                                </div>
                                <Controller
                                    control={control}
                                    name="useGoogleMaps"
                                    render={({ field }) => <Switch checked={field.value} onCheckedChange={field.onChange} />}
                                />
                            </div>

                            {watch("useGoogleMaps") && (
                                <div>
                                    <Label>Coordenadas o Link</Label>
                                    <div className="relative">
                                        <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                        <Input {...register("mapLocation")} className="pl-9" placeholder="https://maps.google.com/... o lat,lng" />
                                    </div>
                                </div>
                            )}
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
                            {areas.length === 0 ? (
                                <p className="text-center text-muted-foreground py-8">No hay áreas configuradas.</p>
                            ) : (
                                areas.map((area, i) => (
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
                                                <Input {...register(`areas.${i}.name` as const, { required: true })} placeholder="Campo, Platea, VIP..." />
                                                <Controller
                                                    control={control}
                                                    name={`areas.${i}.type` as const}
                                                    render={({ field }) => (
                                                        <Select onValueChange={field.onChange} value={field.value}>
                                                            <SelectTrigger><SelectValue /></SelectTrigger>
                                                            <SelectContent>
                                                                <SelectItem value="general">General</SelectItem>
                                                                <SelectItem value="numbered">Numerada</SelectItem>
                                                            </SelectContent>
                                                        </Select>
                                                    )}
                                                />
                                                <Input type="number" {...register(`areas.${i}.price` as const, { required: true })} placeholder="Precio" />
                                                <Input type="number" {...register(`areas.${i}.capacity` as const, { required: true })} placeholder="Capacidad" />
                                            </div>

                                            {/* Filas */}
                                            {watch(`areas.${i}.type`) === "numbered" && (
                                                <div className="pt-4 border-t space-y-3">
                                                    <div className="flex justify-between">
                                                        <Label>Filas</Label>
                                                        <Button type="button" size="sm" variant="outline" onClick={() => handleAddRow(i)}>
                                                            <Plus className="mr-2 h-3 w-3" /> Agregar Fila
                                                        </Button>
                                                    </div>

                                                    {area.rows.map((row) => (
                                                        <div key={row.id} className="grid grid-cols-12 gap-2 items-end">
                                                            <Input className="col-span-3" placeholder="Nombre" value={row.name} onChange={(e) => {
                                                                const newRows = area.rows.map(r => r.id === row.id ? { ...r, name: e.target.value } : r)
                                                                update(i, { ...area, rows: newRows })
                                                            }} />
                                                            <Input className="col-span-4" type="number" placeholder="Inicio" value={row.startSeat}
                                                                onChange={(e) => {
                                                                    const newRows = area.rows.map(r => r.id === row.id ? { ...r, startSeat: Number(e.target.value) } : r)
                                                                    update(i, { ...area, rows: newRows })
                                                                }}
                                                            />
                                                            <Input className="col-span-4" type="number" placeholder="Fin" value={row.endSeat}
                                                                onChange={(e) => {
                                                                    const newRows = area.rows.map(r => r.id === row.id ? { ...r, endSeat: Number(e.target.value) } : r)
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
                                ))
                            )}
                        </CardContent>
                    </Card>

                    {/* === Footer === */}
                    <div className="flex gap-4">
                        <Button type="button" variant="outline" size="lg" asChild>
                            <Link href="/admin/events">Cancelar</Link>
                        </Button>

                        <StarBorder className="flex-1">
                            <Button type="submit" size="lg" className="w-full gradient-brand text-white" disabled={isSubmitting || areas.length === 0}>
                                {isSubmitting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : "Crear Evento"}
                            </Button>
                        </StarBorder>
                    </div>
                </form>
            </div>
        </div>
    )
}
