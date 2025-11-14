package com.ticket.app.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.ticket.app.domain.enums.TicketStatus;
import com.ticket.app.domain.models.EventModel;
import com.ticket.app.domain.models.TicketStatusHistoryModel;

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
    private UUID eventId;
    private UUID eventVenueAreaId;
    private UUID eventVenueSeatId;
    private UUID userId;

    private String code;
    private String qrBase64;

    private BigDecimal price;
    private String currency;
    private BigDecimal discount;
    private BigDecimal finalPrice;

    private TicketStatus status;
    private OffsetDateTime issuedAt;
    private OffsetDateTime checkedInAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime refundedAt;
    private OffsetDateTime expiresAt;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<TicketStatusHistoryModel> history;

    private EventModel event;

}
