package com.ticket.app.domain.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaModel {
    private String id;
    private String name;
    private boolean isGeneralAdmission;
    private Integer capacity;
    private Integer position;
    private BigDecimal priceCents;
    private String currency;
    private Integer availableTickets;
    private Integer totalSeats;
}
