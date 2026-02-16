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
import { toast } from "sonner";
import { couponService } from "@/services/couponService";
import type { CreateCouponRequest } from "@/types/Coupon";
import { DiscountType } from "@/types/enums/DiscountType";

export default function CreateCouponPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);

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

        // Validar que la fecha de inicio no sea anterior a la fecha actual
        const now = new Date();
        const validFrom = new Date(formData.validFrom);
        const validUntil = new Date(formData.validUntil);

        if (validFrom < now) {
            toast.error("La fecha de inicio no puede ser anterior a la fecha actual");
            return;
        }

        if (validFrom >= validUntil) {
            toast.error("La fecha de fin debe ser posterior a la fecha de inicio");
            return;
        }

        setLoading(true);
        
        const dataToSend = {
            ...formData,
            code: formData.code.toUpperCase(),
            eventIds: [], // Siempre aplicar a todos los eventos
        };

        console.log("Enviando cupón:", dataToSend);
        
        try {
            const result = await couponService.createCoupon(dataToSend);
            console.log("Cupón creado exitosamente:", result);
            toast.success("Cupón creado correctamente");
            setLoading(false);
            // Usar replace + refresh para forzar recarga de la página
            router.replace("/admin/coupons");
            router.refresh();
        } catch (error: any) {
            setLoading(false);
            console.error("Error completo:", error);
            console.error("Error response:", error.response);
            console.error("Error message:", error.message);
            const errorMessage = error.response?.data?.message || error.message || "No se pudo crear el cupón";
            toast.error(errorMessage);
        }
    };

    const handleChange = (field: keyof CreateCouponRequest, value: any) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
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
                                pattern="[A-Z0-9\-]+"
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
                                    placeholder="0"
                                    value={formData.discountValue === 0 ? "" : formData.discountValue}
                                    onChange={(e) =>
                                        handleChange("discountValue", e.target.value ? parseFloat(e.target.value) : 0)
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
                    </CardContent>
                </Card>

                {/* Uso Máximo */}
                <Card>
                    <CardHeader>
                        <CardTitle>Límite de Usos</CardTitle>
                        <CardDescription>
                            Define la cantidad máxima de veces que se puede usar este cupón
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="maxUses">
                                Uso Máximo <span className="text-muted-foreground text-sm">(opcional)</span>
                            </Label>
                            <Input
                                id="maxUses"
                                type="number"
                                min={1}
                                step={1}
                                placeholder="Sin límite"
                                value={formData.maxUses ?? ""}
                                onChange={(e) =>
                                    handleChange("maxUses", e.target.value ? parseInt(e.target.value) : undefined)
                                }
                            />
                            <p className="text-sm text-muted-foreground">
                                Dejalo vacío para usos ilimitados
                            </p>
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
                                    min={new Date().toISOString().slice(0, 16)}
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
                                    min={formData.validFrom || new Date().toISOString().slice(0, 16)}
                                    value={formData.validUntil}
                                    onChange={(e) => handleChange("validUntil", e.target.value)}
                                    required
                                />
                            </div>
                        </div>
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
