package com.order.app.services;

import com.order.app.exceptions.coupon.*;
import com.order.app.models.Coupon;
import com.order.app.repositories.CouponRedemptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para validar cupones antes de aplicarlos en órdenes.
 * Implementa las 8 validaciones especificadas en los requisitos.
 */
@Service
public class CouponValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CouponValidationService.class);
    
    private final CouponRedemptionRepository redemptionRepository;
    
    public CouponValidationService(CouponRedemptionRepository redemptionRepository) {
        this.redemptionRepository = redemptionRepository;
    }
    
    /**
     * Valida que un cupón puede ser usado en una orden.
     * Lanza excepciones específicas para cada tipo de error.
     * 
     * @param coupon El cupón a validar
     * @param customerId ID del cliente que intenta usar el cupón
     * @param eventId ID del evento de la orden
     * @param subtotalCents Subtotal de la orden en centavos
     * @param currency Moneda de la orden
     * @throws CouponException Si el cupón no es válido
     */
    public void validateCoupon(Coupon coupon, UUID customerId, UUID eventId, 
                               Long subtotalCents, String currency) {
        
        logger.debug("Validando cupón {} para cliente {}, evento {}, subtotal {}", 
                    coupon.getCode(), customerId, eventId, subtotalCents);
        
        // VALIDACIÓN 1: Existencia (ya validada al obtener el cupón)
        // Si llegamos aquí, el cupón existe
        
        // VALIDACIÓN 2: Estado
        validateStatus(coupon);
        
        // VALIDACIÓN 3: Vigencia
        validateDateRange(coupon);
        
        // VALIDACIÓN 4: Límite Global de Usos
        validateGlobalLimit(coupon);
        
        // VALIDACIÓN 5: Límite por Cliente
        validateCustomerLimit(coupon, customerId);
        
        // VALIDACIÓN 6: Restricción de Eventos
        validateEventRestriction(coupon, eventId);
        
        // VALIDACIÓN 7: Monto Mínimo
        validateMinimumPurchase(coupon, subtotalCents);
        
        // VALIDACIÓN 8: Moneda
        validateCurrency(coupon, currency);
        
        logger.info("Cupón {} validado exitosamente para cliente {}", 
                   coupon.getCode(), customerId);
    }
    
    /**
     * VALIDACIÓN 2: Verifica que el cupón esté en estado ACTIVE.
     */
    private void validateStatus(Coupon coupon) {
        if (coupon.getStatus() != Coupon.CouponStatus.ACTIVE) {
            String message = switch (coupon.getStatus()) {
                case INACTIVE -> "Este cupón no está disponible actualmente";
                case EXPIRED -> "Este cupón ha expirado";
                case EXHAUSTED -> "Este cupón alcanzó su límite de usos";
                case ACTIVE -> "Estado inesperado"; // No debería llegar aquí
            };
            
            logger.warn("Cupón {} rechazado: estado {}", coupon.getCode(), coupon.getStatus());
            throw new CouponInactiveException(message);
        }
    }
    
    /**
     * VALIDACIÓN 3: Verifica que la fecha actual esté dentro del rango de vigencia.
     */
    private void validateDateRange(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        
        if (!coupon.isValidForDate(now)) {
            if (now.isBefore(coupon.getValidFrom())) {
                String message = String.format(
                    "Este cupón aún no está disponible. Válido desde: %s", 
                    coupon.getValidFrom()
                );
                logger.warn("Cupón {} rechazado: aún no válido", coupon.getCode());
                throw new CouponNotYetValidException(message);
            } else {
                String message = String.format(
                    "Este cupón expiró el: %s", 
                    coupon.getValidUntil()
                );
                logger.warn("Cupón {} rechazado: expirado", coupon.getCode());
                throw new CouponExpiredException(message);
            }
        }
    }
    
    /**
     * VALIDACIÓN 4: Verifica que el cupón no haya alcanzado su límite global de usos.
     */
    private void validateGlobalLimit(Coupon coupon) {
        if (coupon.isExhausted()) {
            logger.warn("Cupón {} rechazado: límite global alcanzado ({}/{})", 
                       coupon.getCode(), coupon.getCurrentUses(), coupon.getMaxUses());
            throw new CouponExhaustedException("Este cupón alcanzó su límite de usos");
        }
    }
    
    /**
     * VALIDACIÓN 5: Verifica que el cliente no haya excedido su límite de usos.
     */
    private void validateCustomerLimit(Coupon coupon, UUID customerId) {
        if (coupon.getMaxUsesPerCustomer() != null && customerId != null) {
            long customerUses = redemptionRepository.countByCustomerIdAndCouponId(
                customerId, 
                coupon.getId()
            );
            
            if (customerUses >= coupon.getMaxUsesPerCustomer()) {
                logger.warn("Cupón {} rechazado: cliente {} alcanzó límite ({}/{})", 
                           coupon.getCode(), customerId, customerUses, 
                           coupon.getMaxUsesPerCustomer());
                throw new CouponCustomerLimitException(
                    "Ya has usado este cupón el máximo de veces permitidas"
                );
            }
        }
    }
    
    /**
     * VALIDACIÓN 6: Verifica que el cupón sea válido para el evento específico.
     */
    private void validateEventRestriction(Coupon coupon, UUID eventId) {
        if (!coupon.isValidForEvent(eventId)) {
            logger.warn("Cupón {} rechazado: no válido para evento {}", 
                       coupon.getCode(), eventId);
            throw new CouponEventRestrictionException(
                "Este cupón no es válido para este evento"
            );
        }
    }
    
    /**
     * VALIDACIÓN 7: Verifica que el subtotal cumpla con el monto mínimo requerido.
     */
    private void validateMinimumPurchase(Coupon coupon, Long subtotalCents) {
        if (!coupon.meetsMinimumPurchase(subtotalCents)) {
            String message = String.format(
                "Este cupón requiere una compra mínima de $%.2f. Tu compra es de $%.2f",
                coupon.getMinPurchaseAmountCents() / 100.0,
                subtotalCents / 100.0
            );
            logger.warn("Cupón {} rechazado: monto mínimo no alcanzado ({} < {})", 
                       coupon.getCode(), subtotalCents, coupon.getMinPurchaseAmountCents());
            throw new CouponMinimumPurchaseException(message);
        }
    }
    
    /**
     * VALIDACIÓN 8: Verifica que la moneda coincida (solo para FIXED_AMOUNT).
     */
    private void validateCurrency(Coupon coupon, String currency) {
        if (coupon.getDiscountType() == Coupon.DiscountType.FIXED_AMOUNT) {
            if (!coupon.getCurrency().equalsIgnoreCase(currency)) {
                logger.warn("Cupón {} rechazado: moneda incorrecta ({} != {})", 
                           coupon.getCode(), currency, coupon.getCurrency());
                throw new CouponCurrencyMismatchException(
                    "Este cupón solo aplica para compras en " + coupon.getCurrency()
                );
            }
        }
    }
    
    /**
     * Valida un cupón y retorna el monto de descuento calculado.
     * 
     * @return Monto del descuento en centavos
     */
    public long validateAndCalculateDiscount(Coupon coupon, UUID customerId, UUID eventId,
                                             Long subtotalCents, String currency) {
        validateCoupon(coupon, customerId, eventId, subtotalCents, currency);
        return coupon.calculateDiscount(subtotalCents);
    }
}

