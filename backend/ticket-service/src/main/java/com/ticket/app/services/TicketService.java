package com.ticket.app.services;

import java.util.List;
import java.util.UUID;

import com.ticket.app.domain.models.TicketModel;
import com.ticket.app.dto.request.TicketGenerateRequest;

public interface TicketService {
    TicketModel generateTicket(TicketGenerateRequest request);
    TicketModel validateByQrToken(String qrToken);
    TicketModel validateByCode(String code);
    TicketModel getById(UUID id);
    List<TicketModel> getByUserId(UUID userId);
}

