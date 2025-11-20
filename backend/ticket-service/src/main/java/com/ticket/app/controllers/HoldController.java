package com.ticket.app.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ticket.app.domain.entities.Hold;
import com.ticket.app.dto.request.HoldCreateRequest;
import com.ticket.app.dto.response.HoldResponse;
import com.ticket.app.services.HoldService;
import com.ticket.app.utils.ApiResponseFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/holds")
public class HoldController {

    private final HoldService holdService;
    private final ModelMapper modelMapper;

    public HoldController(HoldService holdService, ModelMapper modelMapper) {
        this.holdService = holdService;
        this.modelMapper = modelMapper;
    }

    // 🟢 Crear un hold (General Admission o asiento numerado)
    @PostMapping
    public ResponseEntity<?> createHold(@RequestBody HoldCreateRequest request) {
        Hold hold = holdService.createHold(
                request.getCustomerId(),
                request.getEventId(),
                request.getEventVenueAreaId(),
                request.getEventVenueSeatId(),
                request.getQuantity()
        );

        HoldResponse response = modelMapper.map(hold, HoldResponse.class);
        return ApiResponseFactory.created("Hold created successfully.", response);
    }

    // 🔁 Convertir hold a CONVERTED (al generar ticket)
    @PutMapping("/convert/{eventId}")
    public ResponseEntity<?> convertHold(@PathVariable UUID eventId) {
        holdService.convertHold(eventId);
        return ApiResponseFactory.success("Hold successfully converted to ticket.", null);
    }

    // ⏱️ Expirar holds vencidos manualmente (para pruebas o cron externo)
    @PostMapping("/expire")
    public ResponseEntity<?> expireHolds() {
        holdService.expireHolds();
        return ApiResponseFactory.success("Expired holds processed successfully.", null);
    }

    // 🔍 Obtener todos los holds activos
    @GetMapping("/active")
    public ResponseEntity<?> getActiveHolds() {
        List<Hold> active = holdService.getAllActive();
        List<HoldResponse> mapped = active.stream()
                .map(h -> modelMapper.map(h, HoldResponse.class))
                .toList();

        return ApiResponseFactory.success("Active holds retrieved successfully.", mapped);
    }
}
