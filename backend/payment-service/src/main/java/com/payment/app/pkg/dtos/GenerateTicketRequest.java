package com.payment.app.pkg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO para solicitar la generación de un ticket al Ticket Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTicketRequest {
    
    private String orderItemId;  // UUID como String
    private String occurrenceId; // UUID como String  
    private String userId;       // UUID como String
    private String venueAreaId;  // UUID como String
    
    // Pricing
    private BigDecimal price;
    private String currency;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    
    // Event timing
    private OffsetDateTime eventStart;
    private OffsetDateTime eventEnd;
}

