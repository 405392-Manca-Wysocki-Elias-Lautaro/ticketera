package com.ticket.app.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ticket.app.domain.models.TicketModel;
import com.ticket.app.dto.request.TicketGenerateRequest;
import com.ticket.app.dto.request.TicketValidateRequest;
import com.ticket.app.dto.response.TicketResponse;
import com.ticket.app.exception.exceptions.InvalidTicketValidationTypeException;
import com.ticket.app.services.TicketService;
import com.ticket.app.utils.ApiResponseFactory;

@RestController
public class TicketController {

    private final TicketService ticketService;
    private final ModelMapper modelMapper;

    public TicketController(TicketService ticketService, ModelMapper modelMapper) {
        this.ticketService = ticketService;
        this.modelMapper = modelMapper;
    }

    // 🎟️ Generar nuevo ticket
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody TicketGenerateRequest request) {
        TicketModel ticketModel = ticketService.generateTicket(request);

        TicketResponse ticketResponse = modelMapper.map(ticketModel, TicketResponse.class);

        return ApiResponseFactory.created("Ticket generado correctamente.", ticketResponse);
    }

    // ✅ Validar ticket por código o QR
    @PostMapping("/validate")
    public ResponseEntity<?> validateTicket(@RequestBody TicketValidateRequest request) {
        TicketModel ticket;

        if ("QR".equalsIgnoreCase(request.getType())) {
            ticket = ticketService.validateByQrToken(request.getValue());
        } else if ("CODE".equalsIgnoreCase(request.getType())) {
            ticket = ticketService.validateByCode(request.getValue());
        } else {
            throw new InvalidTicketValidationTypeException(request.getType());
        }

        return ApiResponseFactory.success("Ticket validated successfully.", ticket);
    }
}
