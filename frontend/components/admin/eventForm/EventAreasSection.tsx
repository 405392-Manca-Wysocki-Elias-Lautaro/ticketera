"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Trash2, Plus } from "lucide-react"
import { Tooltip, TooltipTrigger, TooltipContent } from "@/components/ui/tooltip"
import { Controller } from "react-hook-form"
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import useDecimalFormatter from '@/hooks/useDecimalFormatter'

interface Props {
    control: any
    register: any
    watch: any
    append: any
    remove: any
    setValue: any
    areas: any[]
}

export function EventAreasSection({
    control,
    register,
    watch,
    append,
    remove,
    setValue,
    areas
}: Props) {

    const { handleDecimalInput } = useDecimalFormatter();

    return (
        <Card>
            <CardHeader>
                <div className="flex justify-between items-center">
                    <CardTitle>Áreas y Precios</CardTitle>
                    <Button
                        type="button"
                        size="sm"
                        onClick={() => append({
                            id: crypto.randomUUID(),
                            name: "",
                            isGeneralAdmission: true,
                            priceCents: 0,
                            capacity: 0,
                            position: areas.length + 1,
                            rows: []
                        })}
                    >
                        <Plus className="mr-2 h-4 w-4" /> Agregar Área
                    </Button>
                </div>
            </CardHeader>

            <CardContent className="space-y-6">
                {areas.length === 0 && (
                    <p className="text-center text-muted-foreground py-8">No hay áreas agregadas.</p>
                )}

                {areas.map((area, i) => {
                    const rows = watch(`areas.${i}.rows`) ?? [];

                    return (
                        <Card key={area.id} className="border-2">
                            <CardContent className="pt-6 space-y-4">

                                <div className="flex justify-between items-start">
                                    <h4 className="font-semibold">Área {i + 1}</h4>

                                    <Tooltip>
                                        <TooltipTrigger asChild>
                                            <Button variant="ghost" size="icon" onClick={() => remove(i)}>
                                                <Trash2 className="h-4 w-4 text-destructive" />
                                            </Button>
                                        </TooltipTrigger>
                                        <TooltipContent>Eliminar área</TooltipContent>
                                    </Tooltip>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    <Input
                                        {...register(`areas.${i}.name`)}
                                        placeholder="Campo, Platea Alta..."
                                    />

                                    <Controller
                                        control={control}
                                        name={`areas.${i}.isGeneralAdmission`}
                                        render={({ field }) => (
                                            <Select
                                                onValueChange={(v) => {
                                                    const val = v === "true";
                                                    field.onChange(val);

                                                    if (!val && rows.length === 0) {
                                                        setValue(`areas.${i}.rows`, [
                                                            {
                                                                id: crypto.randomUUID(),
                                                                name: "Fila 1",
                                                                startSeat: 1,
                                                                endSeat: 10
                                                            }
                                                        ]);
                                                    }
                                                    if (val) {
                                                        setValue(`areas.${i}.rows`, []);
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

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

                                    <div>
                                        <Label className="ps-3">Precio</Label>
                                        <div className="flex">
                                            <div className="border flex justify-center items-center rounded-l-lg px-2">
                                                ARS$
                                            </div>
                                            <Controller
                                                control={control}
                                                name={`areas.${i}.priceCents`}
                                                render={({ field }) => (
                                                    <Input
                                                        className="rounded-none rounded-r-lg no-spinner"
                                                        type="text"
                                                        inputMode="decimal"
                                                        value={field.value ?? ""}
                                                        onChange={(e) => handleDecimalInput(e, field.onChange)}
                                                        placeholder="0.00"
                                                    />
                                                )}
                                            />
                                        </div>
                                    </div>

                                    {watch(`areas.${i}.isGeneralAdmission`) && (
                                        <div>
                                            <Label className="ps-3">Capacidad</Label>
                                            <Input
                                                className="no-spinner"
                                                type="number"
                                                {...register(`areas.${i}.capacity`)}
                                            />
                                        </div>
                                    )}

                                </div>

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
                                                        endSeat: 10,
                                                    },
                                                ])
                                            }
                                        >
                                            <Plus className="mr-2 h-4 w-4" /> Agregar fila
                                        </Button>

                                        {rows.map((row, rowIndex) => (
                                            <div key={row.id} className="grid grid-cols-12 gap-2 items-center">

                                                <div className="col-span-3">
                                                    <Label className="ps-3">Nombre de fila</Label>
                                                    <Input
                                                        {...register(`areas.${i}.rows.${rowIndex}.name`)}
                                                    />
                                                </div>

                                                <div className="col-span-4">
                                                    <Label className="ps-3">Inicio</Label>
                                                    <Input
                                                        className='no-spinner'
                                                        type="number"
                                                        {...register(`areas.${i}.rows.${rowIndex}.startSeat`)}
                                                    />
                                                </div>

                                                <div className="col-span-4">
                                                    <Label className="ps-3">Fin</Label>
                                                    <Input
                                                        className='no-spinner'
                                                        type="number"
                                                        {...register(`areas.${i}.rows.${rowIndex}.endSeat`)}
                                                    />
                                                </div>

                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => {
                                                        const newRows = rows.filter(r => r.id !== row.id);
                                                        setValue(`areas.${i}.rows`, newRows);
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
    )
}
