package com.order.app.repositories;

import com.order.app.models.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de redenciones de cupones y estadísticas.
 */
@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, UUID> {
    
    /**
     * Busca una redención por ID de orden.
     */
    Optional<CouponRedemption> findByOrderId(UUID orderId);
    
    /**
     * Lista todas las redenciones de un cupón.
     */
    List<CouponRedemption> findByCouponId(UUID couponId);
    
    /**
     * Lista todas las redenciones de un cliente.
     */
    List<CouponRedemption> findByCustomerId(UUID customerId);
    
    /**
     * Cuenta cuántas veces un cliente usó un cupón específico.
     */
    long countByCustomerIdAndCouponId(UUID customerId, UUID couponId);
    
    /**
     * Cuenta total de redenciones de un cupón.
     */
    long countByCouponId(UUID couponId);
    
    /**
     * Cuenta órdenes pagadas que usaron un cupón.
     */
    @Query("SELECT COUNT(DISTINCT r.orderId) FROM CouponRedemption r " +
           "JOIN Order o ON r.orderId = o.id " +
           "WHERE r.couponId = :couponId AND o.status = 'PAID'")
    long countPaidOrdersByCouponId(@Param("couponId") UUID couponId);
    
    /**
     * Suma total del descuento otorgado por un cupón.
     */
    @Query("SELECT COALESCE(SUM(r.discountAppliedCents), 0) FROM CouponRedemption r " +
           "WHERE r.couponId = :couponId")
    long sumDiscountByCouponId(@Param("couponId") UUID couponId);
    
    /**
     * Suma total de ingresos generados con un cupón (órdenes pagadas).
     */
    @Query("SELECT COALESCE(SUM(o.totalCents), 0) FROM CouponRedemption r " +
           "JOIN Order o ON r.orderId = o.id " +
           "WHERE r.couponId = :couponId AND o.status = 'PAID'")
    long sumRevenueByCouponId(@Param("couponId") UUID couponId);
    
    /**
     * Cuenta clientes únicos que usaron un cupón.
     */
    @Query("SELECT COUNT(DISTINCT r.customerId) FROM CouponRedemption r " +
           "WHERE r.couponId = :couponId")
    long countUniqueCustomersByCouponId(@Param("couponId") UUID couponId);
    
    /**
     * Lista redenciones en un rango de fechas.
     */
    @Query("SELECT r FROM CouponRedemption r " +
           "WHERE r.redeemedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY r.redeemedAt DESC")
    List<CouponRedemption> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Lista redenciones de cupones de un organizador.
     */
    @Query("SELECT r FROM CouponRedemption r " +
           "JOIN Coupon c ON r.couponId = c.id " +
           "WHERE c.organizerId = :organizerId " +
           "ORDER BY r.redeemedAt DESC")
    List<CouponRedemption> findByOrganizerId(@Param("organizerId") UUID organizerId);
    
    /**
     * Lista redenciones de cupones de un organizador en un rango de fechas.
     */
    @Query("SELECT r FROM CouponRedemption r " +
           "JOIN Coupon c ON r.couponId = c.id " +
           "WHERE c.organizerId = :organizerId " +
           "AND r.redeemedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY r.redeemedAt DESC")
    List<CouponRedemption> findByOrganizerIdAndDateRange(
        @Param("organizerId") UUID organizerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Obtiene valor promedio de orden con cupón.
     */
    @Query("SELECT AVG(o.totalCents) FROM CouponRedemption r " +
           "JOIN Order o ON r.orderId = o.id " +
           "WHERE r.couponId = :couponId AND o.status = 'PAID'")
    Double getAverageOrderValueByCouponId(@Param("couponId") UUID couponId);
    
    /**
     * Obtiene las últimas N redenciones de un cupón.
     */
    @Query("SELECT r FROM CouponRedemption r " +
           "WHERE r.couponId = :couponId " +
           "ORDER BY r.redeemedAt DESC")
    List<CouponRedemption> findTopNCouponRedemptions(
        @Param("couponId") UUID couponId,
        org.springframework.data.domain.Pageable pageable
    );
}

