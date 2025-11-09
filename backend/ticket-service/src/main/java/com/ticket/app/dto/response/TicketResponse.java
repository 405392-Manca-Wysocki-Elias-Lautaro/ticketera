package com.ticket.app.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ticket.app.domain.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private UUID id;

    private UUID orderItemId;
    private UUID occurrenceId;
    private UUID eventVenueAreaId;
    private UUID eventVenueSeatId;
    private UUID ticketTypeId;

    private String code;
    private String qrBase64;

    private TicketStatus status;
    private OffsetDateTime issuedAt;
    private OffsetDateTime checkedInAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime refundedAt;
}
