package com.ticket.app.domain.models;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ticket.app.domain.enums.HoldStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldModel {

    private UUID id;

    private UUID customerId;
    private UUID eventId;
    private UUID eventVenueAreaId;
    private UUID eventVenueSeatId;
    private Integer quantity;

    private OffsetDateTime expiresAt;
    private HoldStatus status;

    private UUID convertedTicketId;
    private OffsetDateTime convertedAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
