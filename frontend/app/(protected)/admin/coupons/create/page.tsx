"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { ArrowLeft, Save } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { toast } from "sonner";
import { couponService } from "@/services/couponService";
import { eventService } from "@/services/eventService";
import type { CreateCouponRequest } from "@/types/Coupon";
import { DiscountType } from "@/types/enums/DiscountType";
import type { Event } from "@/types/Event";

export default function CreateCouponPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [events, setEvents] = useState<Event[]>([]);
    const [selectedEvents, setSelectedEvents] = useState<string[]>([]);
    const [applyToAllEvents, setApplyToAllEvents] = useState(true);

    const [formData, setFormData] = useState<CreateCouponRequest>({
        code: "",
        description: "",
        discountType: DiscountType.PERCENTAGE,
        discountValue: 0,
        currency: "ARS",
        maxUses: undefined,
        maxUsesPerCustomer: undefined,
        validFrom: "",
        validUntil: "",
        eventIds: [],
        minPurchaseAmountCents: undefined,
    });

    useEffect(() => {
        loadEvents();
        // Setear fecha de inicio a mañana y fin a 30 días
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        const in30Days = new Date();
        in30Days.setDate(in30Days.getDate() + 30);

        setFormData((prev) => ({
            ...prev,
            validFrom: tomorrow.toISOString().slice(0, 16),
            validUntil: in30Days.toISOString().slice(0, 16),
        }));
    }, []);

    const loadEvents = async () => {
        try {
            const response = await eventService.getAll();
            setEvents(response.data.data);
        } catch (error) {
            toast.error("No se pudieron cargar los eventos");
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Validaciones
        if (!formData.code.trim()) {
            toast.error("El código del cupón es requerido");
            return;
        }

        if (formData.discountValue <= 0) {
            toast.error("El valor del descuento debe ser mayor a cero");
            return;
        }

        if (
            formData.discountType === DiscountType.PERCENTAGE &&
            (formData.discountValue < 1 || formData.discountValue > 100)
        ) {
            toast.error("El descuento porcentual debe estar entre 1 y 100");
            return;
        }

        if (new Date(formData.validFrom) >= new Date(formData.validUntil)) {
            toast.error("La fecha de fin debe ser posterior a la fecha de inicio");
            return;
        }

        try {
            setLoading(true);
            const dataToSend = {
                ...formData,
                code: formData.code.toUpperCase(),
                eventIds: applyToAllEvents ? [] : selectedEvents,
            };

            await couponService.createCoupon(dataToSend);
            toast.success("Cupón creado correctamente");
            router.push("/admin/coupons");
        } catch (error: any) {
            toast.error(error.response?.data?.message || "No se pudo crear el cupón");
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (field: keyof CreateCouponRequest, value: any) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
    };

    const toggleEventSelection = (eventId: string) => {
        setSelectedEvents((prev) =>
            prev.includes(eventId)
                ? prev.filter((id) => id !== eventId)
                : [...prev, eventId]
        );
    };

    return (
        <div className="container mx-auto py-6 max-w-4xl">
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* Header */}
                <div className="flex items-center gap-4">
                    <Button
                        type="button"
                        variant="ghost"
                        size="icon"
                        onClick={() => router.back()}
                    >
                        <ArrowLeft className="h-4 w-4" />
                    </Button>
                    <div className="flex-1">
                        <h1 className="text-3xl font-bold">Crear Cupón de Descuento</h1>
                        <p className="text-muted-foreground">
                            Define los detalles del nuevo cupón promocional
                        </p>
                    </div>
                    <Button type="submit" disabled={loading}>
                        <Save className="mr-2 h-4 w-4" />
                        {loading ? "Guardando..." : "Guardar Cupón"}
                    </Button>
                </div>

                {/* Información Básica */}
                <Card>
                    <CardHeader>
                        <CardTitle>Información Básica</CardTitle>
                        <CardDescription>
                            Código y descripción del cupón
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="code">
                                Código del Cupón <span className="text-red-500">*</span>
                            </Label>
                            <Input
                                id="code"
                                placeholder="VERANO2024"
                                value={formData.code}
                                onChange={(e) =>
                                    handleChange("code", e.target.value.toUpperCase())
                                }
                                pattern="[A-Z0-9-]+"
                                title="Solo letras mayúsculas, números y guiones"
                                required
                            />
                            <p className="text-sm text-muted-foreground">
                                Solo letras mayúsculas, números y guiones
                            </p>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="description">Descripción</Label>
                            <Textarea
                                id="description"
                                placeholder="Descripción del cupón..."
                                value={formData.description}
                                onChange={(e) => handleChange("description", e.target.value)}
                                rows={3}
                            />
                        </div>
                    </CardContent>
                </Card>

                {/* Tipo y Valor del Descuento */}
                <Card>
                    <CardHeader>
                        <CardTitle>Descuento</CardTitle>
                        <CardDescription>
                            Configura el tipo y valor del descuento
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="discountType">
                                    Tipo de Descuento <span className="text-red-500">*</span>
                                </Label>
                                <Select
                                    value={formData.discountType}
                                    onValueChange={(value) =>
                                        handleChange("discountType", value as DiscountType)
                                    }
                                >
                                    <SelectTrigger id="discountType">
                                        <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value={DiscountType.PERCENTAGE}>
                                            Porcentaje (%)
                                        </SelectItem>
                                        <SelectItem value={DiscountType.FIXED_AMOUNT}>
                                            Monto Fijo ($)
                                        </SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="discountValue">
                                    Valor del Descuento <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="discountValue"
                                    type="number"
                                    min={formData.discountType === DiscountType.PERCENTAGE ? 1 : 1}
                                    max={formData.discountType === DiscountType.PERCENTAGE ? 100 : undefined}
                                    step={formData.discountType === DiscountType.PERCENTAGE ? 1 : 0.01}
                                    value={formData.discountValue}
                                    onChange={(e) =>
                                        handleChange("discountValue", parseFloat(e.target.value) || 0)
                                    }
                                    required
                                />
                                <p className="text-sm text-muted-foreground">
                                    {formData.discountType === DiscountType.PERCENTAGE
                                        ? "Entre 1 y 100%"
                                        : "Monto en pesos argentinos"}
                                </p>
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="minPurchaseAmount">
                                Monto Mínimo de Compra (opcional)
                            </Label>
                            <Input
                                id="minPurchaseAmount"
                                type="number"
                                min="0"
                                step="0.01"
                                placeholder="0.00"
                                value={
                                    formData.minPurchaseAmountCents
                                        ? formData.minPurchaseAmountCents / 100
                                        : ""
                                }
                                onChange={(e) =>
                                    handleChange(
                                        "minPurchaseAmountCents",
                                        e.target.value
                                            ? Math.round(parseFloat(e.target.value) * 100)
                                            : undefined
                                    )
                                }
                            />
                            <p className="text-sm text-muted-foreground">
                                Monto mínimo requerido para aplicar el cupón
                            </p>
                        </div>
                    </CardContent>
                </Card>

                {/* Límites de Uso */}
                <Card>
                    <CardHeader>
                        <CardTitle>Límites de Uso</CardTitle>
                        <CardDescription>
                            Controla cuántas veces se puede usar el cupón
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="maxUses">Máximo de Usos Totales (opcional)</Label>
                                <Input
                                    id="maxUses"
                                    type="number"
                                    min="1"
                                    placeholder="Ilimitado"
                                    value={formData.maxUses || ""}
                                    onChange={(e) =>
                                        handleChange(
                                            "maxUses",
                                            e.target.value ? parseInt(e.target.value) : undefined
                                        )
                                    }
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="maxUsesPerCustomer">
                                    Usos por Cliente (opcional)
                                </Label>
                                <Input
                                    id="maxUsesPerCustomer"
                                    type="number"
                                    min="1"
                                    placeholder="Ilimitado"
                                    value={formData.maxUsesPerCustomer || ""}
                                    onChange={(e) =>
                                        handleChange(
                                            "maxUsesPerCustomer",
                                            e.target.value ? parseInt(e.target.value) : undefined
                                        )
                                    }
                                />
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Vigencia */}
                <Card>
                    <CardHeader>
                        <CardTitle>Vigencia</CardTitle>
                        <CardDescription>
                            Define el período de validez del cupón
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="validFrom">
                                    Válido Desde <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="validFrom"
                                    type="datetime-local"
                                    value={formData.validFrom}
                                    onChange={(e) => handleChange("validFrom", e.target.value)}
                                    required
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="validUntil">
                                    Válido Hasta <span className="text-red-500">*</span>
                                </Label>
                                <Input
                                    id="validUntil"
                                    type="datetime-local"
                                    value={formData.validUntil}
                                    onChange={(e) => handleChange("validUntil", e.target.value)}
                                    required
                                />
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Eventos Aplicables */}
                <Card>
                    <CardHeader>
                        <CardTitle>Eventos Aplicables</CardTitle>
                        <CardDescription>
                            Selecciona en qué eventos se puede usar este cupón
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="flex items-center space-x-2">
                            <Checkbox
                                id="allEvents"
                                checked={applyToAllEvents}
                                onCheckedChange={(checked) => {
                                    setApplyToAllEvents(checked as boolean);
                                    if (checked) setSelectedEvents([]);
                                }}
                            />
                            <Label htmlFor="allEvents" className="cursor-pointer">
                                Aplicar a todos mis eventos (actuales y futuros)
                            </Label>
                        </div>

                        {!applyToAllEvents && (
                            <div className="space-y-2">
                                <Label>Seleccionar eventos específicos:</Label>
                                <div className="border rounded-md p-4 max-h-64 overflow-y-auto space-y-2">
                                    {events.length === 0 ? (
                                        <p className="text-sm text-muted-foreground text-center py-4">
                                            No tienes eventos disponibles
                                        </p>
                                    ) : (
                                        events.map((event) => (
                                            <div
                                                key={event.id}
                                                className="flex items-center space-x-2"
                                            >
                                                <Checkbox
                                                    id={`event-${event.id}`}
                                                    checked={selectedEvents.includes(event.id)}
                                                    onCheckedChange={() =>
                                                        toggleEventSelection(event.id)
                                                    }
                                                />
                                                <Label
                                                    htmlFor={`event-${event.id}`}
                                                    className="cursor-pointer flex-1"
                                                >
                                                    {event.title}
                                                </Label>
                                            </div>
                                        ))
                                    )}
                                </div>
                            </div>
                        )}
                    </CardContent>
                </Card>

                {/* Botones de Acción */}
                <div className="flex justify-end gap-4">
                    <Button
                        type="button"
                        variant="outline"
                        onClick={() => router.back()}
                        disabled={loading}
                    >
                        Cancelar
                    </Button>
                    <Button type="submit" disabled={loading}>
                        <Save className="mr-2 h-4 w-4" />
                        {loading ? "Guardando..." : "Crear Cupón"}
                    </Button>
                </div>
            </form>
        </div>
    );
}
