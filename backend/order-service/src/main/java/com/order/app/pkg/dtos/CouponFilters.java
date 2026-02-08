package com.order.app.pkg.dtos;

import com.order.app.models.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Clase para encapsular filtros de búsqueda de cupones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponFilters {
    
    private Coupon.CouponStatus status;
    private UUID eventId;
    private String searchText; // Para buscar por código o descripción
    
    public CouponFilters(String statusStr, String eventIdStr) {
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                this.status = Coupon.CouponStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignorar status inválido
            }
        }
        if (eventIdStr != null && !eventIdStr.isBlank()) {
            try {
                this.eventId = UUID.fromString(eventIdStr);
            } catch (IllegalArgumentException e) {
                // Ignorar UUID inválido
            }
        }
    }
    
    /**
     * Verifica si un cupón coincide con los filtros.
     */
    public boolean matches(Coupon coupon) {
        // Filtro por estado
        if (status != null && coupon.getStatus() != status) {
            return false;
        }
        
        // Filtro por evento
        if (eventId != null && !coupon.isValidForEvent(eventId)) {
            return false;
        }
        
        // Filtro por texto de búsqueda
        if (searchText != null && !searchText.isBlank()) {
            String search = searchText.toLowerCase();
            return coupon.getCode().toLowerCase().contains(search) ||
                   (coupon.getDescription() != null && 
                    coupon.getDescription().toLowerCase().contains(search));
        }
        
        return true;
    }
}

