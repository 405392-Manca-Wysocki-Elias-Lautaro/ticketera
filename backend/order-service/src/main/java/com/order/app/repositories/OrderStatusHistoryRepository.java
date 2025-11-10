package com.order.app.repositories;

import com.order.app.models.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(Long orderId);
    
    List<OrderStatusHistory> findByOrderIdAndToStatusOrderByChangedAtDesc(Long orderId, String toStatus);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id = :orderId ORDER BY osh.changedAt DESC")
    List<OrderStatusHistory> findOrderHistoryByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.changedBy = :userId AND osh.changedAt >= :startDate ORDER BY osh.changedAt DESC")
    List<OrderStatusHistory> findUserActivitySince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.organizerId = :organizerId AND osh.changedAt >= :startDate ORDER BY osh.changedAt DESC")
    List<OrderStatusHistory> findOrganizerActivitySince(@Param("organizerId") Long organizerId, @Param("startDate") LocalDateTime startDate);
}
