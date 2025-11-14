package com.ticket.app.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ticket.app.domain.models.TicketModel;
import com.ticket.app.dto.request.TicketGenerateRequest;
import com.ticket.app.dto.request.TicketValidateRequest;
import com.ticket.app.dto.response.TicketResponse;
import com.ticket.app.exception.exceptions.InvalidTicketValidationTypeException;
import com.ticket.app.services.TicketService;
import com.ticket.app.utils.ApiResponseFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class TicketController {

    private final TicketService ticketService;
    private final ModelMapper modelMapper;

    public TicketController(TicketService ticketService, ModelMapper modelMapper) {
        this.ticketService = ticketService;
        this.modelMapper = modelMapper;
    }

    // 🎟️ Generate new ticket
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody TicketGenerateRequest request) {
        TicketModel ticketModel = ticketService.generateTicket(request);
        TicketResponse ticketResponse = modelMapper.map(ticketModel, TicketResponse.class);
        return ApiResponseFactory.created("Ticket generated successfully.", ticketResponse);
    }

    // ✅ Validate ticket by QR or code
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

    // 🔍 Get ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        TicketModel ticket = ticketService.getById(id);
        TicketResponse ticketResponse = modelMapper.map(ticket, TicketResponse.class);
        return ApiResponseFactory.success("Ticket retrieved successfully.", ticketResponse);
    }

    // 👤 Get tickets by user ID
    @GetMapping("/user")
    public ResponseEntity<?> getByUserId() {
        
        List<TicketModel> tickets = ticketService.getByUserId();

        return ApiResponseFactory.success("User tickets retrieved successfully.", tickets);
    }
}
