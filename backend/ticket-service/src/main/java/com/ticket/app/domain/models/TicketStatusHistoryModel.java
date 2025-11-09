package com.ticket.app.domain.models;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatusHistoryModel {
    private UUID id;
    private UUID ticketId;
    private String fromStatus;
    private String toStatus;
    private UUID updatedUser;
    private OffsetDateTime updatedAt;
    private String note;
}
