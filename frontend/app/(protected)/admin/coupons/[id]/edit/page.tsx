"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
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
import type { Coupon, UpdateCouponRequest } from "@/types/Coupon";
import { CouponStatus } from "@/types/enums/CouponStatus";
import { DiscountType } from "@/types/enums/DiscountType";

export default function EditCouponPage() {
    const router = useRouter();
    const params = useParams();
    const couponId = params.id as string;

    const [loading, setLoading] = useState(false);
    const [loadingCoupon, setLoadingCoupon] = useState(true);
    const [coupon, setCoupon] = useState<Coupon | null>(null);

    const [formData, setFormData] = useState<UpdateCouponRequest>({
        description: "",
        status: CouponStatus.ACTIVE,
        maxUses: undefined,
        maxUsesPerCustomer: undefined,
        validFrom: "",
        validUntil: "",
        eventIds: [],
        minPurchaseAmountCents: undefined,
    });

    useEffect(() => {
        loadCoupon();
    }, [couponId]);

    const loadCoupon = async () => {
        try {
            setLoadingCoupon(true);
            const data = await couponService.getCouponById(couponId);
            setCoupon(data);
            setFormData({
                description: data.description,
                status: data.status,
                maxUses: data.maxUses,
                maxUsesPerCustomer: data.maxUsesPerCustomer,
                validFrom: data.validFrom.slice(0, 16),
                validUntil: data.validUntil.slice(0, 16),
                eventIds: data.eventIds,
                minPurchaseAmountCents: data.minPurchaseAmountCents,
            });
            setSelectedEvents(data.eventIds || []);
            setApplyToAllEvents(!data.eventIds || data.eventIds.length === 0);
        } catch (error) {
            toast.error("No se pudo cargar el cupón");
            router.push("/admin/coupons");
        } finally {
            setLoadingCoupon(false);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Validar que la fecha de inicio no sea anterior a la fecha actual
        const now = new Date();
        const validFrom = new Date(formData.validFrom!);
        const validUntil = new Date(formData.validUntil!);

        if (validFrom < now) {
            toast.error("La fecha de inicio no puede ser anterior a la fecha actual");
            return;
        }

        if (validFrom >= validUntil) {
            toast.error("La fecha de fin debe ser posterior a la fecha de inicio");
            return;
        }

        try {
            setLoading(true);
            const dataToSend = {
                ...formData,
                eventIds: [], // Siempre aplicar a todos los eventos
            };

            await couponService.updateCoupon(couponId, dataToSend);
            toast.success("Cupón actualizado correctamente");
            router.push("/admin/coupons");
        } catch (error: any) {
            toast.error(error.response?.data?.message || "No se pudo actualizar el cupón");
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (field: keyof UpdateCouponRequest, value: any) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
    };

    if (loadingCoupon || !coupon) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        );
    }

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
                        <h1 className="text-3xl font-bold">Editar Cupón</h1>
                        <p className="text-muted-foreground font-mono font-bold">
                            {coupon.code}
                        </p>
                    </div>
                </div>

                {/* Info No Editable */}
                <Card>
                    <CardHeader>
                        <CardTitle>Información del Cupón</CardTitle>
                        <CardDescription>
                            Estos datos no se pueden modificar
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <Label>Código</Label>
                                <div className="font-mono font-bold text-lg">{coupon.code}</div>
                            </div>
                            <div>
                                <Label>Tipo de Descuento</Label>
                                <div className="text-lg">
                                    {coupon.discountType === DiscountType.PERCENTAGE
                                        ? `${coupon.discountValue}%`
                                        : `$${(coupon.discountValue / 100).toFixed(2)}`}
                                </div>
                            </div>
                        </div>
                        <div>
                            <Label>Usos Actuales</Label>
                            <div className="text-lg">
                                {coupon.currentUses}
                                {coupon.maxUses && ` de ${coupon.maxUses} usos`}
                            </div>
                        </div>
                    </CardContent>
                </Card>

                {/* Información Editable */}
                <Card>
                    <CardHeader>
                        <CardTitle>Información Básica</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
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

                        <div className="space-y-2">
                            <Label htmlFor="status">Estado</Label>
                            <Select
                                value={formData.status}
                                onValueChange={(value) =>
                                    handleChange("status", value as CouponStatus)
                                }
                            >
                                <SelectTrigger id="status">
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={CouponStatus.ACTIVE}>Activo</SelectItem>
                                    <SelectItem value={CouponStatus.INACTIVE}>Inactivo</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </CardContent>
                </Card>

                {/* Límites de Uso */}
                <Card>
                    <CardHeader>
                        <CardTitle>Límites de Uso</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="maxUses">Máximo de Usos Totales</Label>
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
                    </CardContent>
                </Card>

                {/* Vigencia */}
                <Card>
                    <CardHeader>
                        <CardTitle>Vigencia</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="validFrom">Válido Desde</Label>
                                <Input
                                    id="validFrom"
                                    type="datetime-local"
                                    min={new Date().toISOString().slice(0, 16)}
                                    value={formData.validFrom}
                                    onChange={(e) => handleChange("validFrom", e.target.value)}
                                />
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="validUntil">Válido Hasta</Label>
                                <Input
                                    id="validUntil"
                                    type="datetime-local"
                                    min={formData.validFrom || new Date().toISOString().slice(0, 16)}
                                    value={formData.validUntil}
                                    onChange={(e) => handleChange("validUntil", e.target.value)}
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
                        {loading ? "Guardando..." : "Guardar Cambios"}
                    </Button>
                </div>
            </form>
        </div>
    );
}
