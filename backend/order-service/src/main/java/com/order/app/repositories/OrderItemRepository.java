package com.order.app.repositories;

import com.order.app.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    
    List<OrderItem> findByOrderIdAndDeletedAtIsNull(UUID orderId);
    
    List<OrderItem> findByEventIdAndDeletedAtIsNull(UUID eventId);
    
    List<OrderItem> findByTicketTypeIdAndDeletedAtIsNull(UUID ticketTypeId);
    
    Optional<OrderItem> findByOrderIdAndVenueSeatIdAndDeletedAtIsNull(UUID orderId, UUID venueSeatId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.venueSeatId = :seatId AND oi.deletedAt IS NULL")
    Optional<OrderItem> findByOrderAndSeat(@Param("orderId") UUID orderId, @Param("seatId") UUID seatId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.eventId = :eventId AND oi.venueSeatId = :seatId AND oi.deletedAt IS NULL")
    Long countBySeatAndEvent(@Param("eventId") UUID eventId, @Param("seatId") UUID seatId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.eventId = :eventId AND oi.ticketTypeId = :ticketTypeId AND oi.deletedAt IS NULL")
    Long sumQuantityByEventAndTicketType(@Param("eventId") UUID eventId, @Param("ticketTypeId") UUID ticketTypeId);
    
    boolean existsByVenueSeatIdAndDeletedAtIsNull(UUID venueSeatId);
}
