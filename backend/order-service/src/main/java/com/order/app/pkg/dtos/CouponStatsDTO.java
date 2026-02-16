package com.order.app.pkg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para estadísticas de un cupón.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponStatsDTO {
    
    private UUID couponId;
    private String couponCode;
    
    // Métricas de uso
    private Long totalRedemptions;
    private Long paidOrders;
    private Long pendingOrders;
    private Long failedOrders;
    private Double conversionRate; // Porcentaje de órdenes pagadas
    
    // Métricas financieras
    private Long totalDiscountGiven; // En centavos
    private Long totalRevenue; // Total de órdenes pagadas en centavos
    private Double averageOrderValue; // Valor promedio de orden con cupón
    
    // Métricas de clientes
    private Long uniqueCustomers;
    
    /**
     * Calcula órdenes no exitosas.
     */
    public Long getUnsuccessfulOrders() {
        if (totalRedemptions == null || paidOrders == null) {
            return 0L;
        }
        return totalRedemptions - paidOrders;
    }
    
    /**
     * Calcula el descuento promedio por orden.
     */
    public Double getAverageDiscountPerOrder() {
        if (totalRedemptions == null || totalRedemptions == 0) {
            return 0.0;
        }
        return totalDiscountGiven != null 
            ? (totalDiscountGiven * 1.0) / totalRedemptions 
            : 0.0;
    }
    
    /**
     * Calcula el ROI aproximado (ingresos / descuento otorgado).
     */
    public Double getROI() {
        if (totalDiscountGiven == null || totalDiscountGiven == 0) {
            return null;
        }
        return totalRevenue != null 
            ? (totalRevenue * 100.0) / totalDiscountGiven 
            : 0.0;
    }
}

