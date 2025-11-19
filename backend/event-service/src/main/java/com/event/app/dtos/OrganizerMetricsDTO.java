package com.event.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para métricas del organizador
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerMetricsDTO {
    
    private Long totalTicketsSold;
    private Long totalRevenueCents;
    private Double totalRevenue;
    private Long activeEventsCount;
    private Long ticketsSoldLastWeek;
}

