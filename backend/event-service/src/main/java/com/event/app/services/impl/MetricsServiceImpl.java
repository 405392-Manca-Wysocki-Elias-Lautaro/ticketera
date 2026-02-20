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
        
        // Nuevas métricas
        Long availableTickets = getAvailableTicketsByOrganizer(organizerId);
        String mostPopularEventName = getMostPopularEventByOrganizer(organizerId);
        String mostProfitableEventName = getMostProfitableEventByOrganizer(organizerId);
        String mostSoldTicketTypeName = getMostSoldTicketTypeByOrganizer(organizerId);
        
        Double totalRevenue = totalRevenueCents != null ? totalRevenueCents / 100.0 : 0.0;
        
        return OrganizerMetricsDTO.builder()
                .totalTicketsSold(totalTicketsSold != null ? totalTicketsSold : 0L)
                .totalRevenueCents(totalRevenueCents != null ? totalRevenueCents : 0L)
                .totalRevenue(totalRevenue)
                .activeEventsCount(activeEventsCount != null ? activeEventsCount : 0L)
                .ticketsSoldLastWeek(ticketsSoldLastWeek != null ? ticketsSoldLastWeek : 0L)
                .availableTickets(availableTickets != null ? availableTickets : 0L)
                .mostPopularEventName(mostPopularEventName)
                .mostProfitableEventName(mostProfitableEventName)
                .mostSoldTicketTypeName(mostSoldTicketTypeName)
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
                AND e.ends_at > :now
        """);
        query.setParameter("organizerId", organizerId);
        query.setParameter("now", LocalDateTime.now());
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
    
    /**
     * Obtiene la cantidad de tickets disponibles (capacidad total - tickets vendidos)
     */
    private Long getAvailableTicketsByOrganizer(UUID organizerId) {
        // Obtener capacidad total de eventos activos del organizador
        Query capacityQuery = entityManager.createNativeQuery("""
            SELECT COALESCE(SUM(COALESCE(a.capacity, 
                (SELECT COUNT(*) FROM events.seats s WHERE s.area_id = a.id)
            )), 0)
            FROM events.events e
            INNER JOIN events.areas a ON a.event_id = e.id
            WHERE e.organizer_id = :organizerId
              AND e.active = true
              AND e.ends_at > :now
        """);
        capacityQuery.setParameter("organizerId", organizerId);
        capacityQuery.setParameter("now", LocalDateTime.now());
        Object capacityResult = capacityQuery.getSingleResult();
        Long totalCapacity = capacityResult != null ? ((Number) capacityResult).longValue() : 0L;
        
        // Obtener tickets vendidos
        Long ticketsSold = getTotalTicketsSoldByOrganizer(organizerId);
        
        // Calcular disponibles
        Long available = totalCapacity - ticketsSold;
        return available < 0 ? 0L : available;
    }
    
    /**
     * Obtiene el nombre del evento más popular (con mayor cantidad de tickets vendidos)
     */
    private String getMostPopularEventByOrganizer(UUID organizerId) {
        Query query = entityManager.createNativeQuery("""
            SELECT e.title
            FROM events.events e
            INNER JOIN orders.order_items oi ON oi.event_id = e.id
            INNER JOIN orders.orders ord ON ord.id = oi.order_id
            WHERE e.organizer_id = :organizerId
                AND ord.status = 'paid'
                AND ord.deleted_at IS NULL
                AND oi.deleted_at IS NULL
                AND e.active = true
            GROUP BY e.id, e.title
            ORDER BY SUM(oi.quantity) DESC
            LIMIT 1
        """);
        query.setParameter("organizerId", organizerId);
        try {
            Object result = query.getSingleResult();
            return result != null ? result.toString() : null;
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
    
    /**
     * Obtiene el nombre del evento más rentable (con mayor recaudación total)
     */
    private String getMostProfitableEventByOrganizer(UUID organizerId) {
        Query query = entityManager.createNativeQuery("""
            SELECT e.title
            FROM events.events e
            INNER JOIN orders.order_items oi ON oi.event_id = e.id
            INNER JOIN orders.orders ord ON ord.id = oi.order_id
            WHERE e.organizer_id = :organizerId
                AND ord.status = 'paid'
                AND ord.deleted_at IS NULL
                AND oi.deleted_at IS NULL
                AND e.active = true
            GROUP BY e.id, e.title
            ORDER BY SUM(oi.unit_price_cents * oi.quantity) DESC
            LIMIT 1
        """);
        query.setParameter("organizerId", organizerId);
        try {
            Object result = query.getSingleResult();
            return result != null ? result.toString() : null;
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
    
    /**
     * Obtiene el nombre del tipo de ticket más vendido
     */
    private String getMostSoldTicketTypeByOrganizer(UUID organizerId) {
        Query query = entityManager.createNativeQuery("""
            SELECT st.name
            FROM events.seat_types st
            INNER JOIN orders.order_items oi ON oi.ticket_type_id = st.id
            INNER JOIN orders.orders ord ON ord.id = oi.order_id
            INNER JOIN events.events e ON e.id = oi.event_id
            WHERE e.organizer_id = :organizerId
                AND ord.status = 'paid'
                AND ord.deleted_at IS NULL
                AND oi.deleted_at IS NULL
                AND e.active = true
            GROUP BY st.id, st.name
            ORDER BY SUM(oi.quantity) DESC
            LIMIT 1
        """);
        query.setParameter("organizerId", organizerId);
        try {
            Object result = query.getSingleResult();
            return result != null ? result.toString() : null;
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
}

