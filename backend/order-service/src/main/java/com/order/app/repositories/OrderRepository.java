package com.order.app.repositories;

import com.order.app.models.Order;
import com.order.app.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    Optional<Order> findByIdAndDeletedAtIsNull(UUID id);
    
    Optional<Order> findByExternalReferenceAndDeletedAtIsNull(String externalReference);
    
    List<Order> findByCustomerIdAndDeletedAtIsNull(UUID customerId);
    
    List<Order> findByOrganizerIdAndDeletedAtIsNull(UUID organizerId);
    
    List<Order> findByStatusAndDeletedAtIsNull(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status AND o.deletedAt IS NULL")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") UUID customerId, @Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.organizerId = :organizerId AND o.status = :status AND o.deletedAt IS NULL")
    Page<Order> findByOrganizerIdAndStatus(@Param("organizerId") UUID organizerId, @Param("status") OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.expiresAt <= :now AND o.status = :status AND o.deletedAt IS NULL")
    List<Order> findExpiredOrders(@Param("now") LocalDateTime now, @Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt <= :endDate AND o.deletedAt IS NULL")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.organizerId = :organizerId AND o.status = :status AND o.deletedAt IS NULL")
    Long countByOrganizerIdAndStatus(@Param("organizerId") UUID organizerId, @Param("status") OrderStatus status);
    
    @Query("SELECT SUM(o.totalCents) FROM Order o WHERE o.organizerId = :organizerId AND o.status = :status AND o.deletedAt IS NULL")
    Long sumTotalByOrganizerIdAndStatus(@Param("organizerId") UUID organizerId, @Param("status") OrderStatus status);
    
    boolean existsByExternalReferenceAndDeletedAtIsNull(String externalReference);
}
