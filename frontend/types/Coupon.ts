import { DiscountType } from './enums/DiscountType';
import { CouponStatus } from './enums/CouponStatus';

export interface Coupon {
    id: string;
    organizerId: string;
    code: string;
    description?: string;
    discountType: DiscountType;
    discountValue: number;
    currency: string;
    maxUses?: number;
    maxUsesPerCustomer?: number;
    currentUses: number;
    validFrom: string;
    validUntil: string;
    eventIds?: string[];
    minPurchaseAmountCents?: number;
    status: CouponStatus;
    createdAt: string;
    updatedAt: string;
    createdBy?: string;
}

export interface CreateCouponRequest {
    code: string;
    description?: string;
    discountType: DiscountType;
    discountValue: number;
    currency?: string;
    maxUses?: number;
    maxUsesPerCustomer?: number;
    validFrom: string;
    validUntil: string;
    eventIds?: string[];
    minPurchaseAmountCents?: number;
}

export interface UpdateCouponRequest {
    description?: string;
    status?: CouponStatus;
    maxUses?: number;
    maxUsesPerCustomer?: number;
    validFrom?: string;
    validUntil?: string;
    eventIds?: string[];
    minPurchaseAmountCents?: number;
}

export interface CouponStats {
    totalRedemptions: number;
    totalDiscountApplied: number;
    currency: string;
    usagePercentage: number;
    redemptionsByDate: Array<{
        date: string;
        count: number;
        totalDiscount: number;
    }>;
}
