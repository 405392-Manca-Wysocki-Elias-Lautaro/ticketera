package com.order.app.services;

import com.order.app.exceptions.coupon.*;
import com.order.app.models.Coupon;
import com.order.app.models.CouponRedemption;
import com.order.app.pkg.dtos.*;
import com.order.app.repositories.CouponRedemptionRepository;
import com.order.app.repositories.CouponRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestión completa de cupones (CRUD, validación, estadísticas).
 */
@Service
@Transactional
public class CouponService {
    
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
    
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final CouponValidationService validationService;
    
    public CouponService(
            CouponRepository couponRepository,
            CouponRedemptionRepository redemptionRepository,
            CouponValidationService validationService) {
        this.couponRepository = couponRepository;
        this.redemptionRepository = redemptionRepository;
        this.validationService = validationService;
    }
    
    // ========================================
    // CRUD OPERATIONS
    // ========================================
    
    /**
     * Crea un nuevo cupón para un organizador.
     */
    public CouponDTO createCoupon(CreateCouponRequest request, UUID organizerId, UUID createdBy) {
        logger.info("Creando cupón {} para organizador {}", request.getCode(), organizerId);
        
        // Normalizar código a mayúsculas
        String normalizedCode = request.getCode().toUpperCase().trim();
        
        // Validar que el código sea único para el organizador
        if (couponRepository.existsByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, normalizedCode)) {
            logger.warn("Intento de crear cupón duplicado: {}", normalizedCode);
            throw new CouponCodeAlreadyExistsException(
                "El código " + normalizedCode + " ya existe para este organizador"
            );
        }
        
        // Crear cupón
        Coupon coupon = Coupon.builder()
            .organizerId(organizerId)
            .code(normalizedCode)
            .description(request.getDescription())
            .discountType(request.getDiscountType())
            .discountValue(request.getDiscountValue())
            .currency(request.getCurrency())
            .maxUses(request.getMaxUses())
            .maxUsesPerCustomer(request.getMaxUsesPerCustomer())
            .validFrom(request.getValidFrom())
            .validUntil(request.getValidUntil())
            .eventIds(request.getEventIds())
            .minPurchaseAmountCents(request.getMinPurchaseAmountCents())
            .status(Coupon.CouponStatus.ACTIVE)
            .createdBy(createdBy)
            .build();
        
        coupon = couponRepository.save(coupon);
        
        logger.info("Cupón {} creado exitosamente con ID {}", normalizedCode, coupon.getId());
        return CouponDTO.fromEntity(coupon);
    }
    
    /**
     * Lista todos los cupones de un organizador con filtros opcionales.
     */
    @Transactional(readOnly = true)
    public List<CouponDTO> getCouponsByOrganizer(UUID organizerId, CouponFilters filters) {
        logger.debug("Listando cupones para organizador {} con filtros: {}", organizerId, filters);
        
        List<Coupon> coupons;
        
        // Si hay filtro de estado, usar query optimizado
        if (filters != null && filters.getStatus() != null) {
            coupons = couponRepository.findByOrganizerIdAndStatusAndDeletedAtIsNull(
                organizerId, 
                filters.getStatus()
            );
        } else {
            coupons = couponRepository.findByOrganizerIdAndDeletedAtIsNull(organizerId);
        }
        
        // Aplicar filtros adicionales en memoria
        return coupons.stream()
            .filter(c -> filters == null || filters.matches(c))
            .map(CouponDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un cupón por ID.
     */
    @Transactional(readOnly = true)
    public CouponDTO getCouponById(UUID couponId, UUID organizerId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
            .orElseThrow(() -> new CouponNotFoundException("Cupón no encontrado"));
        
        // Verificar que pertenece al organizador
        if (!coupon.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedAccessException("No tienes permiso para ver este cupón");
        }
        
        return CouponDTO.fromEntity(coupon);
    }
    
    /**
     * Obtiene un cupón por código (para validación en checkout).
     */
    @Transactional(readOnly = true)
    public Coupon getCouponByCode(String code, UUID organizerId) {
        String normalizedCode = code.toUpperCase().trim();
        return couponRepository.findByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, normalizedCode)
            .orElseThrow(() -> new CouponNotFoundException("Cupón no válido"));
    }
    
    /**
     * Actualiza un cupón existente (solo campos permitidos).
     */
    public CouponDTO updateCoupon(UUID couponId, UpdateCouponRequest request, UUID organizerId) {
        logger.info("Actualizando cupón {}", couponId);
        
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
            .orElseThrow(() -> new CouponNotFoundException("Cupón no encontrado"));
        
        // Verificar pertenencia
        if (!coupon.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedAccessException("No tienes permiso para editar este cupón");
        }
        
        // Actualizar solo campos permitidos
        if (request.getDescription() != null) {
            coupon.setDescription(request.getDescription());
        }
        
        if (request.getStatus() != null) {
            coupon.setStatus(request.getStatus());
        }
        
        if (request.getMaxUses() != null) {
            // No permitir reducir por debajo de usos actuales
            if (request.getMaxUses() >= coupon.getCurrentUses()) {
                coupon.setMaxUses(request.getMaxUses());
            } else {
                throw new IllegalArgumentException(
                    "No se puede reducir el límite por debajo de los usos actuales (" + 
                    coupon.getCurrentUses() + ")"
                );
            }
        }
        
        if (request.getMaxUsesPerCustomer() != null) {
            coupon.setMaxUsesPerCustomer(request.getMaxUsesPerCustomer());
        }
        
        if (request.getValidFrom() != null) {
            coupon.setValidFrom(request.getValidFrom());
        }
        
        if (request.getValidUntil() != null) {
            coupon.setValidUntil(request.getValidUntil());
        }
        
        if (request.getEventIds() != null) {
            coupon.setEventIds(request.getEventIds());
        }
        
        if (request.getMinPurchaseAmountCents() != null) {
            coupon.setMinPurchaseAmountCents(request.getMinPurchaseAmountCents());
        }
        
        coupon = couponRepository.save(coupon);
        
        logger.info("Cupón {} actualizado exitosamente", couponId);
        return CouponDTO.fromEntity(coupon);
    }
    
    /**
     * Elimina (soft delete) un cupón.
     */
    public void deleteCoupon(UUID couponId, UUID organizerId) {
        logger.info("Eliminando cupón {}", couponId);
        
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
            .orElseThrow(() -> new CouponNotFoundException("Cupón no encontrado"));
        
        // Verificar pertenencia
        if (!coupon.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedAccessException("No tienes permiso para eliminar este cupón");
        }
        
        // Soft delete
        coupon.softDelete();
        couponRepository.save(coupon);
        
        logger.info("Cupón {} eliminado (soft delete)", couponId);
    }
    
    // ========================================
    // VALIDATION & REDEMPTION
    // ========================================
    
    /**
     * Valida un cupón para el checkout (sin aplicarlo aún).
     */
    @Transactional(readOnly = true)
    public ValidateCouponResponse validateCouponForCheckout(ValidateCouponRequest request) {
        try {
            // Obtener cupón
            Coupon coupon = getCouponByCode(request.getCode(), request.getOrganizerId());
            
            // Validar
            validationService.validateCoupon(
                coupon,
                request.getCustomerId(),
                request.getEventId(),
                request.getSubtotalCents(),
                request.getCurrency()
            );
            
            // Calcular descuento
            long discountCents = coupon.calculateDiscount(request.getSubtotalCents());
            
            return ValidateCouponResponse.success(CouponDTO.fromEntity(coupon), discountCents);
            
        } catch (CouponException e) {
            logger.debug("Validación de cupón fallida: {}", e.getMessage());
            return ValidateCouponResponse.error(e.getMessage());
        }
    }
    
    /**
     * Registra el uso de un cupón en una orden.
     * Este método debe llamarse DENTRO de la transacción de creación de orden.
     */
    public void redeemCoupon(Coupon coupon, UUID orderId, UUID customerId,
                            long discountAppliedCents, long subtotalCents) {
        logger.info("Registrando redención de cupón {} en orden {}", coupon.getCode(), orderId);
        
        // Incrementar contador de usos
        coupon.incrementUses();
        couponRepository.save(coupon);
        
        // Registrar redención
        CouponRedemption redemption = CouponRedemption.builder()
            .couponId(coupon.getId())
            .orderId(orderId)
            .customerId(customerId)
            .discountAppliedCents(discountAppliedCents)
            .subtotalCents(subtotalCents)
            .build();
        
        redemptionRepository.save(redemption);
        
        logger.info("Redención registrada: cupón {} usado por cliente {}, descuento: {}", 
                   coupon.getCode(), customerId, discountAppliedCents);
    }
    
    // ========================================
    // STATISTICS & REPORTING
    // ========================================
    
    /**
     * Obtiene estadísticas completas de un cupón.
     */
    @Transactional(readOnly = true)
    public CouponStatsDTO getCouponStats(UUID couponId, UUID organizerId) {
        logger.debug("Obteniendo estadísticas para cupón {}", couponId);
        
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
            .orElseThrow(() -> new CouponNotFoundException("Cupón no encontrado"));
        
        if (!coupon.getOrganizerId().equals(organizerId)) {
            throw new UnauthorizedAccessException("No tienes permiso");
        }
        
        // Calcular métricas
        long totalRedemptions = redemptionRepository.countByCouponId(couponId);
        long paidOrders = redemptionRepository.countPaidOrdersByCouponId(couponId);
        long totalDiscountGiven = redemptionRepository.sumDiscountByCouponId(couponId);
        long totalRevenue = redemptionRepository.sumRevenueByCouponId(couponId);
        long uniqueCustomers = redemptionRepository.countUniqueCustomersByCouponId(couponId);
        Double averageOrderValue = redemptionRepository.getAverageOrderValueByCouponId(couponId);
        
        double conversionRate = totalRedemptions > 0 
            ? (paidOrders * 100.0) / totalRedemptions 
            : 0.0;
        
        return CouponStatsDTO.builder()
            .couponId(couponId)
            .couponCode(coupon.getCode())
            .totalRedemptions(totalRedemptions)
            .paidOrders(paidOrders)
            .pendingOrders(totalRedemptions - paidOrders)
            .failedOrders(0L) // TODO: calcular órdenes fallidas
            .conversionRate(conversionRate)
            .totalDiscountGiven(totalDiscountGiven)
            .totalRevenue(totalRevenue)
            .averageOrderValue(averageOrderValue)
            .uniqueCustomers(uniqueCustomers)
            .build();
    }
    
    /**
     * Obtiene reporte de redenciones para un organizador.
     */
    @Transactional(readOnly = true)
    public List<CouponRedemption> getRedemptionsReport(
            UUID organizerId, 
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            UUID couponId) {
        
        logger.debug("Generando reporte de redenciones para organizador {}", organizerId);
        
        if (couponId != null) {
            // Verificar que el cupón pertenece al organizador
            Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Cupón no encontrado"));
            
            if (!coupon.getOrganizerId().equals(organizerId)) {
                throw new UnauthorizedAccessException("No tienes permiso");
            }
            
            return redemptionRepository.findByCouponId(couponId);
        }
        
        if (startDate != null && endDate != null) {
            return redemptionRepository.findByOrganizerIdAndDateRange(organizerId, startDate, endDate);
        }
        
        return redemptionRepository.findByOrganizerId(organizerId);
    }
}
