package com.ticket.app.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ticket.app.domain.enums.HoldStatus;
import lombok.Data;

@Data
public class HoldResponse {
    private UUID id;
    private UUID customerId;
    private UUID occurrenceId;
    private UUID eventVenueAreaId;
    private UUID eventVenueSeatId;
    private Integer quantity;
    private HoldStatus status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime convertedAt;
}
