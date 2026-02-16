"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, TrendingUp, DollarSign, Users, Percent, Clock, CalendarDays, Zap } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { couponService } from "@/services/couponService";
import { toast } from "sonner";
import type { Coupon } from "@/types/Coupon";
import { DiscountType } from "@/types/enums/DiscountType";
import { CouponStatus } from "@/types/enums/CouponStatus";

export default function CouponStatsPage() {
    const router = useRouter();
    const params = useParams();
    const couponId = params.id as string;

    const [coupon, setCoupon] = useState<Coupon | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (couponId) {
            loadCoupon();
        }
    }, [couponId]);

    const loadCoupon = async () => {
        try {
            setLoading(true);
            const data = await couponService.getCouponById(couponId);
            setCoupon(data);
        } catch (error) {
            console.error("Error loading coupon:", error);
            toast.error("No se pudo cargar el cupón");
        } finally {
            setLoading(false);
        }
    };

    const getStatusBadge = (status: CouponStatus) => {
        const styles: Record<string, string> = {
            ACTIVE: "bg-green-100 text-green-800 hover:bg-green-100",
            INACTIVE: "bg-gray-100 text-gray-800 hover:bg-gray-100",
            EXPIRED: "bg-red-100 text-red-800 hover:bg-red-100",
            EXHAUSTED: "bg-orange-100 text-orange-800 hover:bg-orange-100",
        };
        const labels: Record<string, string> = {
            ACTIVE: "Activo",
            INACTIVE: "Inactivo",
            EXPIRED: "Expirado",
            EXHAUSTED: "Agotado",
        };
        return <Badge className={styles[status]}>{labels[status]}</Badge>;
    };

    const formatDiscount = (coupon: Coupon) => {
        if (coupon.discountType === DiscountType.PERCENTAGE) {
            return `${coupon.discountValue}%`;
        }
        return `$${coupon.discountValue.toLocaleString("es-AR")}`;
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString("es-AR", {
            year: "numeric",
            month: "long",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getUsagePercentage = (coupon: Coupon) => {
        if (!coupon.maxUses) return null;
        return Math.round((coupon.currentUses / coupon.maxUses) * 100);
    };

    const getDaysRemaining = (coupon: Coupon) => {
        const now = new Date();
        const until = new Date(coupon.validUntil);
        const diff = until.getTime() - now.getTime();
        if (diff <= 0) return 0;
        return Math.ceil(diff / (1000 * 60 * 60 * 24));
    };

    const getTotalDays = (coupon: Coupon) => {
        const from = new Date(coupon.validFrom);
        const until = new Date(coupon.validUntil);
        return Math.ceil((until.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));
    };

    const getElapsedPercentage = (coupon: Coupon) => {
        const from = new Date(coupon.validFrom);
        const until = new Date(coupon.validUntil);
        const now = new Date();
        const total = until.getTime() - from.getTime();
        const elapsed = now.getTime() - from.getTime();
        if (total <= 0) return 100;
        return Math.min(Math.max(Math.round((elapsed / total) * 100), 0), 100);
    };

    const getAvgUsesPerDay = (coupon: Coupon) => {
        const from = new Date(coupon.validFrom);
        const now = new Date();
        const daysSinceStart = Math.max(1, Math.ceil((now.getTime() - from.getTime()) / (1000 * 60 * 60 * 24)));
        if (coupon.currentUses === 0) return 0;
        return (coupon.currentUses / daysSinceStart).toFixed(1);
    };

    if (loading) {
        return (
            <div className="container mx-auto py-6 max-w-4xl">
                <div className="flex items-center justify-center h-64">
                    <p className="text-muted-foreground">Cargando estadísticas...</p>
                </div>
            </div>
        );
    }

    if (!coupon) {
        return (
            <div className="container mx-auto py-6 max-w-4xl">
                <div className="flex items-center justify-center h-64">
                    <p className="text-muted-foreground">Cupón no encontrado</p>
                </div>
            </div>
        );
    }

    const usagePercentage = getUsagePercentage(coupon);
    const daysRemaining = getDaysRemaining(coupon);
    const totalDays = getTotalDays(coupon);
    const elapsedPercentage = getElapsedPercentage(coupon);
    const avgUsesPerDay = getAvgUsesPerDay(coupon);

    return (
        <div className="container mx-auto py-6 max-w-4xl space-y-6">
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
                    <div className="flex items-center gap-3">
                        <h1 className="text-3xl font-bold font-mono">{coupon.code}</h1>
                        {getStatusBadge(coupon.status)}
                    </div>
                    <p className="text-muted-foreground">
                        {coupon.description || "Sin descripción"}
                    </p>
                </div>
            </div>

            {/* Stats Cards - Fila 1 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Descuento</CardTitle>
                        {coupon.discountType === DiscountType.PERCENTAGE ? (
                            <Percent className="h-4 w-4 text-muted-foreground" />
                        ) : (
                            <DollarSign className="h-4 w-4 text-muted-foreground" />
                        )}
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{formatDiscount(coupon)}</div>
                        <p className="text-xs text-muted-foreground">
                            {coupon.discountType === DiscountType.PERCENTAGE
                                ? "Porcentaje"
                                : "Monto fijo"}
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Usos Totales</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">
                            {coupon.currentUses}
                            {coupon.maxUses && (
                                <span className="text-base font-normal text-muted-foreground">
                                    {" "}/ {coupon.maxUses}
                                </span>
                            )}
                        </div>
                        <p className="text-xs text-muted-foreground">
                            {coupon.maxUses
                                ? `${coupon.maxUses - coupon.currentUses} restantes`
                                : "Sin límite"}
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Tasa de Uso</CardTitle>
                        <TrendingUp className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">
                            {usagePercentage !== null ? `${usagePercentage}%` : "∞"}
                        </div>
                        {usagePercentage !== null && (
                            <div className="mt-2 w-full bg-gray-200 rounded-full h-2">
                                <div
                                    className={`h-2 rounded-full ${
                                        usagePercentage >= 90
                                            ? "bg-red-500"
                                            : usagePercentage >= 70
                                            ? "bg-orange-500"
                                            : "bg-green-500"
                                    }`}
                                    style={{ width: `${Math.min(usagePercentage, 100)}%` }}
                                />
                            </div>
                        )}
                        <p className="text-xs text-muted-foreground mt-1">
                            {usagePercentage !== null
                                ? "del límite total"
                                : "Usos ilimitados"}
                        </p>
                    </CardContent>
                </Card>
            </div>

            {/* Stats Cards - Fila 2 */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Días Restantes</CardTitle>
                        <Clock className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className={`text-2xl font-bold ${daysRemaining === 0 ? "text-red-500" : daysRemaining <= 7 ? "text-orange-500" : ""}`}>
                            {daysRemaining === 0 ? "Vencido" : `${daysRemaining} días`}
                        </div>
                        <p className="text-xs text-muted-foreground">
                            de {totalDays} días de vigencia total
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Promedio Diario</CardTitle>
                        <Zap className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{avgUsesPerDay}</div>
                        <p className="text-xs text-muted-foreground">
                            usos por día desde el inicio
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Tiempo Transcurrido</CardTitle>
                        <CalendarDays className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{elapsedPercentage}%</div>
                        <div className="mt-2 w-full bg-gray-200 rounded-full h-2">
                            <div
                                className="h-2 rounded-full bg-blue-500"
                                style={{ width: `${elapsedPercentage}%` }}
                            />
                        </div>
                        <p className="text-xs text-muted-foreground mt-1">
                            del período de vigencia
                        </p>
                    </CardContent>
                </Card>
            </div>

            {/* Detalles */}
            <Card>
                <CardHeader>
                    <CardTitle>Detalles del Cupón</CardTitle>
                    <CardDescription>Información completa del cupón</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-4">
                            <div>
                                <p className="text-sm font-medium text-muted-foreground">Válido Desde</p>
                                <p className="text-sm">{formatDate(coupon.validFrom)}</p>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-muted-foreground">Válido Hasta</p>
                                <p className="text-sm">{formatDate(coupon.validUntil)}</p>
                            </div>
                        </div>
                        <div className="space-y-4">
                            <div>
                                <p className="text-sm font-medium text-muted-foreground">Creado</p>
                                <p className="text-sm">{formatDate(coupon.createdAt)}</p>
                            </div>
                            <div>
                                <p className="text-sm font-medium text-muted-foreground">Estado</p>
                                {getStatusBadge(coupon.status)}
                            </div>
                        </div>
                    </div>
                </CardContent>
            </Card>

            {/* Botón Volver */}
            <div className="flex justify-end">
                <Button variant="outline" onClick={() => router.push("/admin/coupons")}>
                    Volver a la lista
                </Button>
            </div>
        </div>
    );
}
