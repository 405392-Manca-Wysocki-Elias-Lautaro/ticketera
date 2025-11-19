package com.event.app.services.impl;

import com.event.app.dtos.OrganizerMetricsDTO;
import com.event.app.services.IMetricsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MetricsServiceImpl implements IMetricsService {
    
    private final EntityManager entityManager;
    
    public MetricsServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrganizerMetricsDTO getOrganizerMetrics(UUID organizerId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        
        Long totalTicketsSold = getTotalTicketsSoldByOrganizer(organizerId);
        Long totalRevenueCents = getTotalRevenueCentsByOrganizer(organizerId);
        Long activeEventsCount = getActiveEventsCountByOrganizer(organizerId);
        Long ticketsSoldLastWeek = getTicketsSoldLastWeekByOrganizer(organizerId, oneWeekAgo);
        
        Double totalRevenue = totalRevenueCents != null ? totalRevenueCents / 100.0 : 0.0;
        
        return OrganizerMetricsDTO.builder()
                .totalTicketsSold(totalTicketsSold != null ? totalTicketsSold : 0L)
                .totalRevenueCents(totalRevenueCents != null ? totalRevenueCents : 0L)
                .totalRevenue(totalRevenue)
                .activeEventsCount(activeEventsCount != null ? activeEventsCount : 0L)
                .ticketsSoldLastWeek(ticketsSoldLastWeek != null ? ticketsSoldLastWeek : 0L)
                .build();
    }
    
    private Long getTotalTicketsSoldByOrganizer(UUID organizerId) {
        Query query = entityManager.createNativeQuery("""
            SELECT COALESCE(SUM(oi.quantity), 0)
            FROM orders.order_items oi
            INNER JOIN orders.orders ord ON ord.id = oi.order_id
            INNER JOIN events.events e ON e.id = oi.event_id
            WHERE e.organizer_id = :organizerId
                AND ord.status = 'paid'
                AND ord.deleted_at IS NULL
                AND oi.deleted_at IS NULL
                AND e.active = true
        """);
        query.setParameter("organizerId", organizerId);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    private Long getTotalRevenueCentsByOrganizer(UUID organizerId) {
        Query query = entityManager.createNativeQuery("""
            SELECT COALESCE(SUM(o.total_cents), 0)
            FROM orders.orders o
            WHERE o.organizer_id = :organizerId
                AND o.status = 'paid'
                AND o.deleted_at IS NULL
        """);
        query.setParameter("organizerId", organizerId);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    private Long getActiveEventsCountByOrganizer(UUID organizerId) {
        Query query = entityManager.createNativeQuery("""
            SELECT COUNT(*)
            FROM events.events e
            WHERE e.organizer_id = :organizerId
                AND e.active = true
        """);
        query.setParameter("organizerId", organizerId);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
    
    private Long getTicketsSoldLastWeekByOrganizer(UUID organizerId, LocalDateTime oneWeekAgo) {
        Query query = entityManager.createNativeQuery("""
            SELECT COALESCE(SUM(oi.quantity), 0)
            FROM orders.order_items oi
            INNER JOIN orders.orders ord ON ord.id = oi.order_id
            INNER JOIN events.events e ON e.id = oi.event_id
            WHERE e.organizer_id = :organizerId
                AND ord.status = 'paid'
                AND oi.created_at >= :oneWeekAgo
                AND ord.deleted_at IS NULL
                AND oi.deleted_at IS NULL
                AND e.active = true
        """);
        query.setParameter("organizerId", organizerId);
        query.setParameter("oneWeekAgo", oneWeekAgo);
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }
}

