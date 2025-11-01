"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { useAuth } from "@/hooks/auth/useAuth"
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
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { ArrowLeft, Plus, Trash2, CalendarIcon, Clock, Upload, LinkIcon, MapPin } from "lucide-react"
import Link from "next/link"
import { format } from "date-fns"
import { es } from "date-fns/locale"
import { cn } from "@/lib/utils"
import { AdminSidebar } from '@/components/AdminSidebar'
import { RoleCode } from '@/types/enums/RoleCode'

interface AreaForm {
  id: string
  name: string
  type: "general" | "numbered"
  price: number
  capacity: number
  rows: RowForm[]
}

interface RowForm {
  id: string
  name: string
  startSeat: number
  endSeat: number
}

export default function CreateEventPage() {
  const router = useRouter()
  const { user, isLoading } = useAuth()
  const [areas, setAreas] = useState<AreaForm[]>([])
  const [isSaving, setIsSaving] = useState(false)
  const [startDate, setStartDate] = useState<Date>()
  const [endDate, setEndDate] = useState<Date>()
  const [startTime, setStartTime] = useState("")
  const [endTime, setEndTime] = useState("")
  const [imageType, setImageType] = useState<"url" | "upload">("url")
  const [imageUrl, setImageUrl] = useState("")
  const [imageFile, setImageFile] = useState<File | null>(null)
  const [useGoogleMaps, setUseGoogleMaps] = useState(false)
  const [mapLocation, setMapLocation] = useState("")

  useEffect(() => {
    if (!isLoading && (!user || user.role.code !== RoleCode.ADMIN)) {
      router.push("/dashboard")
    }
  }, [user, isLoading, router])

  const addArea = () => {
    setAreas([
      ...areas,
      {
        id: Math.random().toString(36).substr(2, 9),
        name: "",
        type: "general",
        price: 0,
        capacity: 0,
        rows: [],
      },
    ])
  }

  const removeArea = (id: string) => {
    setAreas(areas.filter((a) => a.id !== id))
  }

  const updateArea = (id: string, updates: Partial<AreaForm>) => {
    setAreas(areas.map((a) => (a.id === id ? { ...a, ...updates } : a)))
  }

  const addRow = (areaId: string) => {
    setAreas(
      areas.map((a) =>
        a.id === areaId
          ? {
              ...a,
              rows: [
                ...a.rows,
                {
                  id: Math.random().toString(36).substr(2, 9),
                  name: "",
                  startSeat: 1,
                  endSeat: 10,
                },
              ],
            }
          : a,
      ),
    )
  }

  const removeRow = (areaId: string, rowId: string) => {
    setAreas(areas.map((a) => (a.id === areaId ? { ...a, rows: a.rows.filter((r) => r.id !== rowId) } : a)))
  }

  const updateRow = (areaId: string, rowId: string, updates: Partial<RowForm>) => {
    setAreas(
      areas.map((a) =>
        a.id === areaId
          ? {
              ...a,
              rows: a.rows.map((r) => (r.id === rowId ? { ...r, ...updates } : r)),
            }
          : a,
      ),
    )
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSaving(true)
    await new Promise((resolve) => setTimeout(resolve, 1500))
    router.push("/admin/events")
  }

  if (isLoading || !user || user.role.code !== RoleCode.ADMIN) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
      </div>
    )
  }

  return (
    <TooltipProvider>
      <div className="flex min-h-screen">
        {/* Sidebar */}
        <AdminSidebar />

        <div className="flex-1 overflow-auto">
          <main className="container mx-auto px-4 py-8 max-w-4xl">
            <Button variant="ghost" asChild className="mb-6 cursor-pointer">
              <Link href="/admin/events">
                <ArrowLeft className="mr-2 h-4 w-4" />
                Volver a eventos
              </Link>
            </Button>

            <h1 className="text-3xl font-bold mb-8 gradient-text">Crear Nuevo Evento</h1>

            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Basic Info */}
              <Card>
                <CardHeader>
                  <CardTitle>Información Básica</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="title">Nombre del Evento</Label>
                    <Input id="title" placeholder="Festival de Rock 2025" required />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="description">Descripción</Label>
                    <Textarea id="description" placeholder="Describe tu evento..." rows={4} required />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="category">Categoría</Label>
                      <Select required>
                        <SelectTrigger>
                          <SelectValue placeholder="Selecciona una categoría" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="musica">Música</SelectItem>
                          <SelectItem value="comedia">Comedia</SelectItem>
                          <SelectItem value="tecnologia">Tecnología</SelectItem>
                          <SelectItem value="deportes">Deportes</SelectItem>
                          <SelectItem value="teatro">Teatro</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>

                    {/* Image Upload/URL Tabs */}
                    <div className="space-y-2">
                      <Label>Imagen del Evento</Label>
                      <Tabs value={imageType} onValueChange={(v) => setImageType(v as "url" | "upload")}>
                        <TabsList className="grid w-full grid-cols-2">
                          <TabsTrigger value="url" className="cursor-pointer">
                            <LinkIcon className="mr-2 h-4 w-4" />
                            URL
                          </TabsTrigger>
                          <TabsTrigger value="upload" className="cursor-pointer">
                            <Upload className="mr-2 h-4 w-4" />
                            Subir Archivo
                          </TabsTrigger>
                        </TabsList>
                        <TabsContent value="url" className="space-y-2">
                          <Input
                            type="url"
                            placeholder="https://ejemplo.com/imagen.jpg"
                            value={imageUrl}
                            onChange={(e) => setImageUrl(e.target.value)}
                          />
                        </TabsContent>
                        <TabsContent value="upload" className="space-y-2">
                          <Input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setImageFile(e.target.files?.[0] || null)}
                            className="cursor-pointer"
                          />
                          {imageFile && (
                            <p className="text-sm text-muted-foreground">Archivo seleccionado: {imageFile.name}</p>
                          )}
                        </TabsContent>
                      </Tabs>
                    </div>
                  </div>
                </CardContent>
              </Card>

              {/* Date & Location */}
              <Card>
                <CardHeader>
                  <CardTitle>Fecha y Ubicación</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  {/* Date Pickers with Calendar Component */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label>Fecha de Inicio</Label>
                      <Popover>
                        <PopoverTrigger asChild>
                          <Button
                            variant="outline"
                            className={cn(
                              "w-full justify-start text-left font-normal cursor-pointer",
                              !startDate && "text-muted-foreground",
                            )}
                          >
                            <CalendarIcon className="mr-2 h-4 w-4" />
                            {startDate ? format(startDate, "PPP", { locale: es }) : "Selecciona una fecha"}
                          </Button>
                        </PopoverTrigger>
                        <PopoverContent className="w-auto p-0">
                          <Calendar mode="single" selected={startDate} onSelect={setStartDate} initialFocus />
                        </PopoverContent>
                      </Popover>
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="startTime">Hora de Inicio</Label>
                      <div className="relative">
                        <Clock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                          id="startTime"
                          type="time"
                          value={startTime}
                          onChange={(e) => setStartTime(e.target.value)}
                          className="pl-9 cursor-pointer"
                          required
                        />
                      </div>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label>Fecha de Fin (opcional)</Label>
                      <Popover>
                        <PopoverTrigger asChild>
                          <Button
                            variant="outline"
                            className={cn(
                              "w-full justify-start text-left font-normal cursor-pointer",
                              !endDate && "text-muted-foreground",
                            )}
                          >
                            <CalendarIcon className="mr-2 h-4 w-4" />
                            {endDate ? format(endDate, "PPP", { locale: es }) : "Selecciona una fecha"}
                          </Button>
                        </PopoverTrigger>
                        <PopoverContent className="w-auto p-0">
                          <Calendar mode="single" selected={endDate} onSelect={setEndDate} initialFocus />
                        </PopoverContent>
                      </Popover>
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="endTime">Hora de Fin (opcional)</Label>
                      <div className="relative">
                        <Clock className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                          id="endTime"
                          type="time"
                          value={endTime}
                          onChange={(e) => setEndTime(e.target.value)}
                          className="pl-9 cursor-pointer"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="location">Lugar</Label>
                    <Input id="location" placeholder="Estadio Nacional" required />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="address">Dirección Completa</Label>
                    <Input id="address" placeholder="Av. Principal 1234, Ciudad" required />
                  </div>

                  {/* Google Maps Toggle */}
                  <div className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="space-y-0.5">
                      <Label htmlFor="google-maps" className="cursor-pointer">
                        Usar Google Maps
                      </Label>
                      <p className="text-sm text-muted-foreground">
                        Permite seleccionar ubicación en el mapa (función experimental)
                      </p>
                    </div>
                    <Switch id="google-maps" checked={useGoogleMaps} onCheckedChange={setUseGoogleMaps} />
                  </div>

                  {useGoogleMaps && (
                    <div className="space-y-2">
                      <Label htmlFor="map-location">Coordenadas o Link de Google Maps</Label>
                      <div className="relative">
                        <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                          id="map-location"
                          placeholder="https://maps.google.com/... o lat,lng"
                          value={mapLocation}
                          onChange={(e) => setMapLocation(e.target.value)}
                          className="pl-9"
                        />
                      </div>
                      <p className="text-xs text-muted-foreground">
                        Pega el link de Google Maps o las coordenadas (ej: -34.603722, -58.381592)
                      </p>
                    </div>
                  )}
                </CardContent>
              </Card>

              {/* Areas */}
              <Card>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <CardTitle>Áreas y Precios</CardTitle>
                    <Button
                      type="button"
                      onClick={addArea}
                      variant="outline"
                      size="sm"
                      className="cursor-pointer bg-transparent"
                    >
                      <Plus className="mr-2 h-4 w-4" />
                      Agregar Área
                    </Button>
                  </div>
                </CardHeader>
                <CardContent className="space-y-6">
                  {areas.length === 0 ? (
                    <p className="text-center text-muted-foreground py-8">
                      No hay áreas configuradas. Agrega al menos una área para tu evento.
                    </p>
                  ) : (
                    areas.map((area, areaIndex) => (
                      <Card key={area.id} className="border-2">
                        <CardContent className="pt-6 space-y-4">
                          <div className="flex items-start justify-between gap-4">
                            <h4 className="font-semibold">Área {areaIndex + 1}</h4>
                            {/* Tooltip to Delete Button */}
                            <Tooltip>
                              <TooltipTrigger asChild>
                                <Button
                                  type="button"
                                  variant="ghost"
                                  size="icon"
                                  onClick={() => removeArea(area.id)}
                                  className="text-destructive hover:text-destructive cursor-pointer"
                                >
                                  <Trash2 className="h-4 w-4" />
                                </Button>
                              </TooltipTrigger>
                              <TooltipContent>
                                <p>Eliminar área</p>
                              </TooltipContent>
                            </Tooltip>
                          </div>

                          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="space-y-2">
                              <Label>Nombre del Área</Label>
                              <Input
                                placeholder="Campo, Platea, VIP..."
                                value={area.name}
                                onChange={(e) => updateArea(area.id, { name: e.target.value })}
                                required
                              />
                            </div>

                            <div className="space-y-2">
                              <Label>Tipo</Label>
                              <Select
                                value={area.type}
                                onValueChange={(value: "general" | "numbered") =>
                                  updateArea(area.id, { type: value, rows: value === "general" ? [] : area.rows })
                                }
                              >
                                <SelectTrigger className="cursor-pointer">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                  <SelectItem value="general" className="cursor-pointer">
                                    Admisión General
                                  </SelectItem>
                                  <SelectItem value="numbered" className="cursor-pointer">
                                    Asientos Numerados
                                  </SelectItem>
                                </SelectContent>
                              </Select>
                            </div>

                            <div className="space-y-2">
                              <Label>Precio</Label>
                              <Input
                                type="number"
                                placeholder="5000"
                                value={area.price || ""}
                                onChange={(e) => updateArea(area.id, { price: Number(e.target.value) })}
                                required
                              />
                            </div>

                            <div className="space-y-2">
                              <Label>Capacidad</Label>
                              <Input
                                type="number"
                                placeholder="500"
                                value={area.capacity || ""}
                                onChange={(e) => updateArea(area.id, { capacity: Number(e.target.value) })}
                                required
                              />
                            </div>
                          </div>

                          {/* Rows for Numbered Areas */}
                          {area.type === "numbered" && (
                            <div className="space-y-4 pt-4 border-t">
                              <div className="flex items-center justify-between">
                                <Label>Filas</Label>
                                <Button
                                  type="button"
                                  onClick={() => addRow(area.id)}
                                  variant="outline"
                                  size="sm"
                                  className="cursor-pointer"
                                >
                                  <Plus className="mr-2 h-3 w-3" />
                                  Agregar Fila
                                </Button>
                              </div>

                              {area.rows.map((row, rowIndex) => (
                                <div key={row.id} className="grid grid-cols-12 gap-2 items-end">
                                  <div className="col-span-3 space-y-2">
                                    <Label className="text-xs">Nombre</Label>
                                    <Input
                                      placeholder="A"
                                      value={row.name}
                                      onChange={(e) => updateRow(area.id, row.id, { name: e.target.value })}
                                      required
                                    />
                                  </div>
                                  <div className="col-span-4 space-y-2">
                                    <Label className="text-xs">Asiento Inicio</Label>
                                    <Input
                                      type="number"
                                      placeholder="1"
                                      value={row.startSeat || ""}
                                      onChange={(e) =>
                                        updateRow(area.id, row.id, { startSeat: Number(e.target.value) })
                                      }
                                      required
                                    />
                                  </div>
                                  <div className="col-span-4 space-y-2">
                                    <Label className="text-xs">Asiento Fin</Label>
                                    <Input
                                      type="number"
                                      placeholder="50"
                                      value={row.endSeat || ""}
                                      onChange={(e) => updateRow(area.id, row.id, { endSeat: Number(e.target.value) })}
                                      required
                                    />
                                  </div>
                                  <div className="col-span-1">
                                    {/* Tooltip to Delete Row Button */}
                                    <Tooltip>
                                      <TooltipTrigger asChild>
                                        <Button
                                          type="button"
                                          variant="ghost"
                                          size="icon"
                                          onClick={() => removeRow(area.id, row.id)}
                                          className="text-destructive hover:text-destructive cursor-pointer"
                                        >
                                          <Trash2 className="h-4 w-4" />
                                        </Button>
                                      </TooltipTrigger>
                                      <TooltipContent>
                                        <p>Eliminar fila</p>
                                      </TooltipContent>
                                    </Tooltip>
                                  </div>
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

              <div className="flex gap-4">
                <Button
                  type="submit"
                  className="flex-1 gradient-brand text-white cursor-pointer"
                  size="lg"
                  disabled={isSaving || areas.length === 0}
                >
                  {isSaving ? (
                    <>
                      <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
                      Creando Evento...
                    </>
                  ) : (
                    "Crear Evento"
                  )}
                </Button>
                <Button type="button" variant="outline" size="lg" asChild className="cursor-pointer bg-transparent">
                  <Link href="/admin/events">Cancelar</Link>
                </Button>
              </div>
            </form>
          </main>
        </div>
      </div>
    </TooltipProvider>
  )
}
