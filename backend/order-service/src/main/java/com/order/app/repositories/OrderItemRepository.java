package com.order.app.repositories;

import com.order.app.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderIdAndDeletedAtIsNull(Long orderId);
    
    List<OrderItem> findByOccurrenceIdAndDeletedAtIsNull(Long occurrenceId);
    
    List<OrderItem> findByTicketTypeIdAndDeletedAtIsNull(Long ticketTypeId);
    
    Optional<OrderItem> findByOrderIdAndEventVenueSeatIdAndDeletedAtIsNull(Long orderId, Long eventVenueSeatId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.eventVenueSeatId = :seatId AND oi.deletedAt IS NULL")
    Optional<OrderItem> findByOrderAndSeat(@Param("orderId") Long orderId, @Param("seatId") Long seatId);
    
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.occurrenceId = :occurrenceId AND oi.eventVenueSeatId = :seatId AND oi.deletedAt IS NULL")
    Long countBySeatAndOccurrence(@Param("occurrenceId") Long occurrenceId, @Param("seatId") Long seatId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.occurrenceId = :occurrenceId AND oi.ticketTypeId = :ticketTypeId AND oi.deletedAt IS NULL")
    Long sumQuantityByOccurrenceAndTicketType(@Param("occurrenceId") Long occurrenceId, @Param("ticketTypeId") Long ticketTypeId);
    
    boolean existsByEventVenueSeatIdAndDeletedAtIsNull(Long eventVenueSeatId);
}
