package com.event.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para detalle de evento (GET /events/{id})
 * Incluye toda la información del evento + áreas con precios y disponibilidad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDetailDTO {

    private UUID id;
    private UUID organizerId;
    private String title;
    private String slug;
    private String description;
    private String coverUrl;
    private String status;
    private LocalDateTime createdAt;

    // Información de categoría
    private UUID categoryId;
    private String categoryName;
    private String categoryDescription;

    // Información de venue
    private String venueName;
    private String venueDescription;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private BigDecimal lat;
    private BigDecimal lng;

    // Información de fechas
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    // Áreas con precios y disponibilidad
    private List<AreaDetailDTO> areas;

    // Resumen de disponibilidad
    private Integer totalAvailableTickets;
    private Integer totalCapacity;
    private Long minPriceCents;
    private Long maxPriceCents;
    private String currency;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AreaDetailDTO {
        private UUID id;
        private String name;
        private Boolean isGeneralAdmission;
        private Integer capacity;
        private Integer position;
        private Long priceCents;
        private String currency;
        private Integer availableTickets;
        private Integer totalSeats;
    }
}

