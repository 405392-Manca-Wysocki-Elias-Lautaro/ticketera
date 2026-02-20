import api from "@/lib/api";
import type { ApiResponse } from "@/types/Response/Apiresponse";
import type { Coupon, CreateCouponRequest, UpdateCouponRequest, CouponStats } from "@/types/Coupon";

const BASE_URL = "/orders/coupons";

export const couponService = {
    /**
     * Obtiene todos los cupones del organizador actual
     */
    async getCoupons(status?: string, eventId?: string): Promise<Coupon[]> {
        try {
            const params = new URLSearchParams();
            if (status) params.append('status', status);
            if (eventId) params.append('eventId', eventId);

            const response = await api.get<ApiResponse<Coupon[]>>(
                `${BASE_URL}${params.toString() ? `?${params.toString()}` : ''}`
            );
            return response.data.data;
        } catch (error) {
            console.error("Error fetching coupons:", error);
            throw error;
        }
    },

    /**
     * Obtiene un cupón específico por ID
     */
    async getCouponById(couponId: string): Promise<Coupon> {
        try {
            const response = await api.get<ApiResponse<Coupon>>(`${BASE_URL}/${couponId}`);
            return response.data.data;
        } catch (error) {
            console.error(`Error fetching coupon ${couponId}:`, error);
            throw error;
        }
    },

    /**
     * Crea un nuevo cupón
     */
    async createCoupon(data: CreateCouponRequest): Promise<Coupon> {
        try {
            const response = await api.post<ApiResponse<Coupon>>(BASE_URL, data);
            return response.data.data;
        } catch (error) {
            console.error("Error creating coupon:", error);
            throw error;
        }
    },

    /**
     * Actualiza un cupón existente
     */
    async updateCoupon(couponId: string, data: UpdateCouponRequest): Promise<Coupon> {
        try {
            const response = await api.patch<ApiResponse<Coupon>>(
                `${BASE_URL}/${couponId}`,
                data
            );
            return response.data.data;
        } catch (error) {
            console.error(`Error updating coupon ${couponId}:`, error);
            throw error;
        }
    },

    /**
     * Elimina (soft delete) un cupón
     */
    async deleteCoupon(couponId: string): Promise<void> {
        try {
            await api.delete(`${BASE_URL}/${couponId}`);
        } catch (error) {
            console.error(`Error deleting coupon ${couponId}:`, error);
            throw error;
        }
    },

    /**
     * Obtiene estadísticas de uso de un cupón
     */
    async getCouponStats(couponId: string): Promise<CouponStats> {
        try {
            const response = await api.get<ApiResponse<CouponStats>>(
                `${BASE_URL}/${couponId}/stats`
            );
            return response.data.data;
        } catch (error) {
            console.error(`Error fetching stats for coupon ${couponId}:`, error);
            throw error;
        }
    },

    /**
     * Valida un código de cupón para un evento específico
     */
    async validateCoupon(code: string, eventId: string, subtotalCents: number, organizerId?: string, customerId?: string): Promise<{
        valid: boolean;
        discountCents?: number;
        errorMessage?: string;
        coupon?: any;
    }> {
        try {
            const response = await api.post<{
                valid: boolean;
                discountCents?: number;
                errorMessage?: string;
                coupon?: any;
            }>(`${BASE_URL}/validate`, {
                code,
                eventId,
                subtotalCents,
                organizerId: organizerId,
                currency: "ARS",
                customerId,
            });
            return response.data;
        } catch (error) {
            console.error("Error validating coupon:", error);
            throw error;
        }
    },

    /**
     * Duplica un cupón existente (útil para crear variaciones)
     */
    async duplicateCoupon(couponId: string): Promise<Coupon> {
        try {
            const response = await api.post<ApiResponse<Coupon>>(
                `${BASE_URL}/${couponId}/duplicate`
            );
            return response.data.data;
        } catch (error) {
            console.error(`Error duplicating coupon ${couponId}:`, error);
            throw error;
        }
    }
};
