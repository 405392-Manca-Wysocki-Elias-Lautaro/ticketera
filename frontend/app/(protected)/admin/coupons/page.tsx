"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Plus, Search, Trash2, BarChart3 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
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
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { couponService } from "@/services/couponService";
import type { Coupon } from "@/types/Coupon";
import { CouponStatus } from "@/types/enums/CouponStatus";
import { DiscountType } from "@/types/enums/DiscountType";

export default function CouponsPage() {
    const router = useRouter();
    const [coupons, setCoupons] = useState<Coupon[]>([]);
    const [filteredCoupons, setFilteredCoupons] = useState<Coupon[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [statusFilter, setStatusFilter] = useState<string>("all");
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [couponToDelete, setCouponToDelete] = useState<Coupon | null>(null);

    useEffect(() => {
        loadCoupons();
    }, []);

    // Recargar cupones cuando la página se vuelve visible
    useEffect(() => {
        const handleFocus = () => {
            loadCoupons();
        };

        window.addEventListener('focus', handleFocus);
        
        // También recargar cuando el componente se monta
        return () => {
            window.removeEventListener('focus', handleFocus);
        };
    }, []);

    useEffect(() => {
        filterCoupons();
    }, [coupons, searchTerm, statusFilter]);

    const loadCoupons = async () => {
        try {
            setLoading(true);
            const data = await couponService.getCoupons();
            setCoupons(data || []);
        } catch (error) {
            console.error("Error loading coupons:", error);
            toast.error("No se pudieron cargar los cupones");
            setCoupons([]);
        } finally {
            setLoading(false);
        }
    };

    const filterCoupons = () => {
        if (!coupons || !Array.isArray(coupons)) {
            setFilteredCoupons([]);
            return;
        }
        
        let filtered = [...coupons];

        // Filtro por búsqueda
        if (searchTerm) {
            filtered = filtered.filter(
                (coupon) =>
                    coupon.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
                    coupon.description?.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }

        // Filtro por estado
        if (statusFilter !== "all") {
            filtered = filtered.filter((coupon) => coupon.status === statusFilter);
        }

        setFilteredCoupons(filtered);
    };

    const handleDelete = async () => {
        if (!couponToDelete) return;

        try {
            await couponService.deleteCoupon(couponToDelete.id);
            toast.success("Cupón eliminado correctamente");
            loadCoupons();
        } catch (error) {
            toast.error("No se pudo eliminar el cupón");
        } finally {
            setDeleteDialogOpen(false);
            setCouponToDelete(null);
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

        return (
            <Badge className={styles[status]}>
                {labels[status]}
            </Badge>
        );
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
            month: "short",
            day: "numeric",
        });
    };

    return (
        <div className="container mx-auto py-6 space-y-6">
            {/* Header */}
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-3xl font-bold">Cupones de Descuento</h1>
                    <p className="text-muted-foreground">
                        Gestiona cupones promocionales para tus eventos
                    </p>
                </div>
                <Button onClick={() => router.push("/admin/coupons/create")}>
                    <Plus className="mr-2 h-4 w-4" />
                    Crear Cupón
                </Button>
            </div>

            {/* Estadísticas Rápidas */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-sm font-medium">
                            Total Cupones
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{coupons?.length || 0}</div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-sm font-medium">Activos</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-green-600">
                            {coupons?.filter((c) => c.status === CouponStatus.ACTIVE).length || 0}
                        </div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-sm font-medium">Expirados</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-red-600">
                            {coupons?.filter((c) => c.status === CouponStatus.EXPIRED).length || 0}
                        </div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle className="text-sm font-medium">Agotados</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-orange-600">
                            {coupons?.filter((c) => c.status === CouponStatus.EXHAUSTED).length || 0}
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Filtros */}
            <Card>
                <CardHeader>
                    <CardTitle>Filtros</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="flex gap-4">
                        <div className="flex-1">
                            <div className="relative">
                                <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                                <Input
                                    placeholder="Buscar por código o descripción..."
                                    className="pl-8"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>
                        </div>
                        <Select value={statusFilter} onValueChange={setStatusFilter}>
                            <SelectTrigger className="w-[200px]">
                                <SelectValue placeholder="Estado" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">Todos los estados</SelectItem>
                                <SelectItem value="ACTIVE">Activo</SelectItem>
                                <SelectItem value="INACTIVE">Inactivo</SelectItem>
                                <SelectItem value="EXPIRED">Expirado</SelectItem>
                                <SelectItem value="EXHAUSTED">Agotado</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                </CardContent>
            </Card>

            {/* Tabla de Cupones */}
            <Card>
                <CardHeader>
                    <CardTitle>Listado de Cupones</CardTitle>
                    <CardDescription>
                        {filteredCoupons.length} cupón(es) encontrado(s)
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    {loading ? (
                        <div className="text-center py-8">Cargando cupones...</div>
                    ) : filteredCoupons.length === 0 ? (
                        <div className="text-center py-8 text-muted-foreground">
                            No se encontraron cupones
                        </div>
                    ) : (
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Código</TableHead>
                                    <TableHead>Descripción</TableHead>
                                    <TableHead>Descuento</TableHead>
                                    <TableHead>Usos</TableHead>
                                    <TableHead>Vigencia</TableHead>
                                    <TableHead>Estado</TableHead>
                                    <TableHead className="text-right">Acciones</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {filteredCoupons.map((coupon) => (
                                    <TableRow key={coupon.id}>
                                        <TableCell className="font-mono font-bold">
                                            {coupon.code}
                                        </TableCell>
                                        <TableCell>
                                            {coupon.description || "-"}
                                        </TableCell>
                                        <TableCell className="font-semibold">
                                            {formatDiscount(coupon)}
                                        </TableCell>
                                        <TableCell>
                                            {coupon.currentUses}
                                            {coupon.maxUses && ` / ${coupon.maxUses}`}
                                        </TableCell>
                                        <TableCell>
                                            <div className="text-sm">
                                                <div>{formatDate(coupon.validFrom)}</div>
                                                <div className="text-muted-foreground">
                                                    hasta {formatDate(coupon.validUntil)}
                                                </div>
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            {getStatusBadge(coupon.status)}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end gap-2">
                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() =>
                                                        router.push(`/admin/coupons/${coupon.id}/stats`)
                                                    }
                                                    title="Ver estadísticas"
                                                >
                                                    <BarChart3 className="h-4 w-4" />
                                                </Button>
                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    onClick={() => {
                                                        setCouponToDelete(coupon);
                                                        setDeleteDialogOpen(true);
                                                    }}
                                                    title="Eliminar"
                                                >
                                                    <Trash2 className="h-4 w-4 text-red-600" />
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    )}
                </CardContent>
            </Card>

            {/* Dialog de Confirmación de Eliminación */}
            <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>¿Estás seguro?</AlertDialogTitle>
                        <AlertDialogDescription>
                            Esta acción eliminará el cupón &quot;{couponToDelete?.code}&quot;.
                            Esta acción no se puede deshacer.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancelar</AlertDialogCancel>
                        <AlertDialogAction onClick={handleDelete}>
                            Eliminar
                        </AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    );
}
