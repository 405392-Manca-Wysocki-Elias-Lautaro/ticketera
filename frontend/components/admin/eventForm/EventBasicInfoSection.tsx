"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Label } from "@/components/ui/label"
import { Controller } from "react-hook-form"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import type { Category } from "@/types/Category"

interface Props {
    control: any
    register: any
    categories: Category[]
}

export function EventBasicInfoSection({ control, register, categories }: Props) {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Información Básica</CardTitle>
            </CardHeader>

            <CardContent className="space-y-4">
                <div>
                    <Label className="ps-3">Título del Evento</Label>
                    <Input {...register("title", { required: true })} />
                </div>

                <div>
                    <Label className="ps-3">Descripción</Label>
                    <Textarea {...register("description", { required: true })} rows={4} />
                </div>

                <div>
                    <Label className="ps-3">Categoría</Label>
                    <Controller
                        control={control}
                        name="categoryId"
                        render={({ field }) => (
                            <Select onValueChange={field.onChange} value={field.value}>
                                <SelectTrigger>
                                    <SelectValue placeholder="Selecciona una categoría" />
                                </SelectTrigger>
                                <SelectContent>
                                    {categories.map((c) => (
                                        <SelectItem key={c.id} value={c.id}>
                                            {c.name}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        )}
                    />
                </div>

                <div>
                    <Label className="ps-3">Imagen (URL)</Label>
                    <Input type="url" {...register("coverUrl")} placeholder="https://..." />
                </div>
            </CardContent>
        </Card>
    )
}
