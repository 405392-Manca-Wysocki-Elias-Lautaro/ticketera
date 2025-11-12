package com.event.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para lista de eventos (GET /events)
 * Incluye: categoría, fecha inicio, fecha fin, precio mínimo, lugar, total de entradas disponibles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryDTO {

    private UUID id;
    private UUID organizerId;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private String status;

    // Información de categoría
    private UUID categoryId;
    private String categoryName;

    // Información de venue
    private String venueName;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private BigDecimal lat;
    private BigDecimal lng;

    // Información de fechas
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    // Información de pricing y disponibilidad
    private Long minPriceCents;
    private String currency;
    private Integer totalAvailableTickets;
    private Integer totalCapacity;
}

