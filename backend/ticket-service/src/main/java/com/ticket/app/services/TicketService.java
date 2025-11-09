package com.ticket.app.services;

import java.util.UUID;

import com.ticket.app.domain.models.TicketModel;

public interface TicketService {
    TicketModel generateTicket(UUID orderItemId, UUID occurrenceId);
    TicketModel validateByQrToken(String qrToken);
    TicketModel validateByCode(String code);
}

