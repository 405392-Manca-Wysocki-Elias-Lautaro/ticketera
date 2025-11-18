package com.order.app.repositories;

import com.order.app.models.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
    
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(UUID orderId);
    
    List<OrderStatusHistory> findByOrderIdAndToStatusOrderByChangedAtDesc(UUID orderId, String toStatus);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id = :orderId ORDER BY osh.changedAt DESC")
    List<OrderStatusHistory> findOrderHistoryByOrderId(@Param("orderId") UUID orderId);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.changedBy = :userId AND osh.changedAt >= :startDate ORDER BY osh.changedAt DESC")
    List<OrderStatusHistory> findUserActivitySince(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.organizerId = :organizerId AND osh.changedAt >= :startDate ORDER BY osh.changedAt DESC")
    List<OrderStatusHistory> findOrganizerActivitySince(@Param("organizerId") UUID organizerId, @Param("startDate") LocalDateTime startDate);
}
