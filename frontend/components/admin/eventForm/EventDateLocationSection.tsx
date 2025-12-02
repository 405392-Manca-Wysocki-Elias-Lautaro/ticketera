"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Controller } from "react-hook-form"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { CalendarIcon } from "lucide-react"
import { format } from "date-fns"
import { es } from "date-fns/locale"
import { cn } from "@/lib/utils"
import { Textarea } from '@/components/ui/textarea'
import { Button } from '@/components/ui/button'

interface Props {
    control: any
    register: any
    watch: any
}

export function EventDateLocationSection({ control, register }: Props) {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Fecha y Ubicación</CardTitle>
            </CardHeader>

            <CardContent className="space-y-4">

                {/* === START DATE === */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <Label className="ps-3">Fecha de Inicio</Label>
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
                        <Label className="ps-3">Hora de Inicio</Label>
                        <Input type="time" {...register("startTime", { required: true })} />
                    </div>
                </div>

                {/* === END DATE === */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <Label className="ps-3">Fecha de Fin (opcional)</Label>
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
                        <Label className="ps-3">Hora de Fin</Label>
                        <Input type="time" {...register("endTime")} />
                    </div>
                </div>

                <div>
                    <Label className="ps-3">Nombre del Lugar</Label>
                    <Input {...register("venueName", { required: true })} />
                </div>

                <div>
                    <Label className="ps-3">Descripción del Lugar</Label>
                    <Textarea {...register("venueDescription")} rows={3} />
                </div>

                <div>
                    <Label className="ps-3">Dirección</Label>
                    <Input {...register("addressLine", { required: true })} />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                        <Label className="ps-3">Ciudad</Label>
                        <Input {...register("city", { required: true })} placeholder="Ciudad" />
                    </div>
                    <div>
                        <Label className="ps-3">Provincia</Label>
                        <Input {...register("state", { required: true })} placeholder="Provincia" />
                    </div>
                    <div>
                        <Label className="ps-3">País</Label>
                        <Input {...register("country", { required: true })} placeholder="País" />
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}
