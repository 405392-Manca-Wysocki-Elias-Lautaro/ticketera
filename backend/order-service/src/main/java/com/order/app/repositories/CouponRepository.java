package com.order.app.repositories;

import com.order.app.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones CRUD de cupones.
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    
    /**
     * Busca un cupón por ID ignorando los eliminados.
     */
    Optional<Coupon> findByIdAndDeletedAtIsNull(UUID id);
    
    /**
     * Busca un cupón por código y organizador (case-insensitive).
     */
    @Query("SELECT c FROM Coupon c WHERE c.organizerId = :organizerId " +
           "AND UPPER(c.code) = UPPER(:code) AND c.deletedAt IS NULL")
    Optional<Coupon> findByOrganizerIdAndCodeAndDeletedAtIsNull(
        @Param("organizerId") UUID organizerId, 
        @Param("code") String code
    );
    
    /**
     * Verifica si existe un cupón con ese código para un organizador.
     */
    @Query("SELECT COUNT(c) > 0 FROM Coupon c WHERE c.organizerId = :organizerId " +
           "AND UPPER(c.code) = UPPER(:code) AND c.deletedAt IS NULL")
    boolean existsByOrganizerIdAndCodeAndDeletedAtIsNull(
        @Param("organizerId") UUID organizerId, 
        @Param("code") String code
    );
    
    /**
     * Lista todos los cupones de un organizador (no eliminados).
     */
    List<Coupon> findByOrganizerIdAndDeletedAtIsNull(UUID organizerId);
    
    /**
     * Lista cupones de un organizador filtrados por estado.
     */
    List<Coupon> findByOrganizerIdAndStatusAndDeletedAtIsNull(
        UUID organizerId, 
        Coupon.CouponStatus status
    );
    
    /**
     * Lista cupones activos que contienen un eventId específico.
     */
    @Query("SELECT c FROM Coupon c WHERE c.organizerId = :organizerId " +
           "AND c.status = 'ACTIVE' " +
           "AND c.deletedAt IS NULL " +
           "AND (:eventId = ANY(c.eventIds) OR c.eventIds IS NULL OR SIZE(c.eventIds) = 0)")
    List<Coupon> findActiveByOrganizerAndEvent(
        @Param("organizerId") UUID organizerId,
        @Param("eventId") UUID eventId
    );
    
    /**
     * Lista cupones que están por expirar (próximos N días).
     */
    @Query("SELECT c FROM Coupon c WHERE c.organizerId = :organizerId " +
           "AND c.status = 'ACTIVE' " +
           "AND c.validUntil BETWEEN :now AND :futureDate " +
           "AND c.deletedAt IS NULL")
    List<Coupon> findExpiringCoupons(
        @Param("organizerId") UUID organizerId,
        @Param("now") LocalDateTime now,
        @Param("futureDate") LocalDateTime futureDate
    );
    
    /**
     * Cuenta cupones activos de un organizador.
     */
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.organizerId = :organizerId " +
           "AND c.status = 'ACTIVE' AND c.deletedAt IS NULL")
    long countActiveByOrganizerId(@Param("organizerId") UUID organizerId);
    
    /**
     * Busca cupones que deberían marcarse como expirados.
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = 'ACTIVE' " +
           "AND c.validUntil < :now AND c.deletedAt IS NULL")
    List<Coupon> findCouponsToExpire(@Param("now") LocalDateTime now);
}

