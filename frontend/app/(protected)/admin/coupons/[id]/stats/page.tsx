"use client";

import { useState, useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { ArrowLeft, TrendingUp, Users, DollarSign, Calendar } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { couponService } from "@/services/couponService";
import type { Coupon, CouponStats } from "@/types/Coupon";
import { CouponStatus } from "@/types/enums/CouponStatus";
import { DiscountType } from "@/types/enums/DiscountType";

export default function CouponStatsPage() {
    const router = useRouter();
    const params = useParams();
    const couponId = params.id as string;

    const [loading, setLoading] = useState(true);
    const [coupon, setCoupon] = useState<Coupon | null>(null);
    const [stats, setStats] = useState<CouponStats | null>(null);

    useEffect(() => {
        loadData();
    }, [couponId]);

    const loadData = async () => {
        try {
            setLoading(true);
            const [couponData, statsData] = await Promise.all([
                couponService.getCouponById(couponId),
                couponService.getCouponStats(couponId),
            ]);
            setCoupon(couponData);
            setStats(statsData);
        } catch (error) {
            toast.error("No se pudieron cargar las estadísticas");
            router.push("/admin/coupons");
        } finally {
            setLoading(false);
        }
    };

    const getStatusBadge = (status: CouponStatus) => {
        const styles = {
            ACTIVE: "bg-green-500",
            INACTIVE: "bg-gray-500",
            EXPIRED: "bg-red-500",
            EXHAUSTED: "bg-orange-500",
        };

        const labels = {
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
        return `$${(coupon.discountValue / 100).toFixed(2)}`;
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

    if (loading || !coupon || !stats) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
            </div>
        );
    }

    return (
        <div className="container mx-auto py-6 max-w-6xl space-y-6">
            {/* Header */}
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" onClick={() => router.back()}>
                    <ArrowLeft className="h-4 w-4" />
                </Button>
                <div className="flex-1">
                    <h1 className="text-3xl font-bold">Estadísticas del Cupón</h1>
                    <p className="text-muted-foreground font-mono font-bold text-xl">
                        {coupon.code}
                    </p>
                </div>
                <div>{getStatusBadge(coupon.status)}</div>
            </div>

            {/* Detalles del Cupón */}
            <Card>
                <CardHeader>
                    <CardTitle>Detalles del Cupón</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <div>
                            <p className="text-sm text-muted-foreground">Descuento</p>
                            <p className="text-2xl font-bold">{formatDiscount(coupon)}</p>
                        </div>
                        <div>
                            <p className="text-sm text-muted-foreground">Usos</p>
                            <p className="text-2xl font-bold">
                                {coupon.currentUses}
                                {coupon.maxUses && (
                                    <span className="text-sm text-muted-foreground">
                                        {" "}
                                        / {coupon.maxUses}
                                    </span>
                                )}
                            </p>
                        </div>
                        <div>
                            <p className="text-sm text-muted-foreground">Válido Desde</p>
                            <p className="text-sm font-medium">{formatDate(coupon.validFrom)}</p>
                        </div>
                        <div>
                            <p className="text-sm text-muted-foreground">Válido Hasta</p>
                            <p className="text-sm font-medium">{formatDate(coupon.validUntil)}</p>
                        </div>
                    </div>
                    {coupon.description && (
                        <div className="mt-4">
                            <p className="text-sm text-muted-foreground">Descripción</p>
                            <p className="text-sm">{coupon.description}</p>
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* Estadísticas de Uso */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">
                            Total Redenciones
                        </CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.totalRedemptions}</div>
                        <p className="text-xs text-muted-foreground">
                            {stats.usagePercentage.toFixed(1)}% del límite
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">
                            Descuento Total Aplicado
                        </CardTitle>
                        <DollarSign className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">
                            ${(stats.totalDiscountApplied / 100).toFixed(2)}
                        </div>
                        <p className="text-xs text-muted-foreground">
                            Ahorro total de clientes
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">
                            Promedio por Uso
                        </CardTitle>
                        <TrendingUp className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">
                            $
                            {stats.totalRedemptions > 0
                                ? (stats.totalDiscountApplied / stats.totalRedemptions / 100).toFixed(
                                      2
                                  )
                                : "0.00"}
                        </div>
                        <p className="text-xs text-muted-foreground">Por redención</p>
                    </CardContent>
                </Card>
            </div>

            {/* Redenciones por Fecha */}
            {stats.redemptionsByDate && stats.redemptionsByDate.length > 0 && (
                <Card>
                    <CardHeader>
                        <CardTitle>Redenciones por Fecha</CardTitle>
                        <CardDescription>
                            Historial de uso del cupón a lo largo del tiempo
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-2">
                            {stats.redemptionsByDate.map((item, index) => (
                                <div
                                    key={index}
                                    className="flex items-center justify-between p-3 border rounded-lg"
                                >
                                    <div className="flex items-center gap-3">
                                        <Calendar className="h-4 w-4 text-muted-foreground" />
                                        <div>
                                            <p className="font-medium">
                                                {new Date(item.date).toLocaleDateString("es-AR", {
                                                    weekday: "long",
                                                    year: "numeric",
                                                    month: "long",
                                                    day: "numeric",
                                                })}
                                            </p>
                                            <p className="text-sm text-muted-foreground">
                                                {item.count} uso(s)
                                            </p>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <p className="font-semibold">
                                            ${(item.totalDiscount / 100).toFixed(2)}
                                        </p>
                                        <p className="text-sm text-muted-foreground">
                                            Descuento total
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Sin Datos */}
            {stats.totalRedemptions === 0 && (
                <Card>
                    <CardContent className="py-12">
                        <div className="text-center text-muted-foreground">
                            <p className="text-lg mb-2">Este cupón aún no ha sido usado</p>
                            <p className="text-sm">
                                Las estadísticas aparecerán cuando alguien use el cupón
                            </p>
                        </div>
                    </CardContent>
                </Card>
            )}
        </div>
    );
}
