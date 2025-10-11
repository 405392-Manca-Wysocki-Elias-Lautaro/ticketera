package com.event.app.controllers;

import com.event.app.dtos.GenerateSeatsRequestDTO;
import com.event.app.dtos.VenueAreaDTO;
import com.event.app.dtos.VenueSeatDTO;
import com.event.app.models.VenueArea;
import com.event.app.models.VenueSeat;
import com.event.app.services.IVenueAreaService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/venue-areas")
public class VenueAreaController {

    private final IVenueAreaService venueAreaService;
    private final ModelMapper modelMapper;

    public VenueAreaController(IVenueAreaService venueAreaService, ModelMapper modelMapper) {
        this.venueAreaService = venueAreaService;
        this.modelMapper = modelMapper;
    }

    /**
     * Crear una nueva área de venue
     */
    @PostMapping
    public ResponseEntity<VenueAreaDTO> createVenueArea(@Valid @RequestBody VenueAreaDTO venueAreaDTO) {
        VenueArea venueArea = venueAreaService.createVenueArea(venueAreaDTO);
        VenueAreaDTO response = modelMapper.map(venueArea, VenueAreaDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtener un área de venue por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VenueAreaDTO> getVenueAreaById(@PathVariable Long id) {
        return venueAreaService.getVenueAreaById(id)
                .map(venueArea -> ResponseEntity.ok(modelMapper.map(venueArea, VenueAreaDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener todas las áreas de venue
     */
    @GetMapping
    public ResponseEntity<List<VenueAreaDTO>> getAllVenueAreas(
            @RequestParam(required = false) UUID venueId) {
        
        List<VenueArea> venueAreas;
        
        if (venueId != null) {
            venueAreas = venueAreaService.getVenueAreasByVenueId(venueId);
        } else {
            venueAreas = venueAreaService.getAllVenueAreas();
        }
        
        List<VenueAreaDTO> response = venueAreas.stream()
                .map(venueArea -> modelMapper.map(venueArea, VenueAreaDTO.class))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar un área de venue
     */
    @PutMapping("/{id}")
    public ResponseEntity<VenueAreaDTO> updateVenueArea(
            @PathVariable Long id,
            @Valid @RequestBody VenueAreaDTO venueAreaDTO) {
        VenueArea updated = venueAreaService.updateVenueArea(id, venueAreaDTO);
        VenueAreaDTO response = modelMapper.map(updated, VenueAreaDTO.class);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un área de venue
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenueArea(@PathVariable Long id) {
        venueAreaService.deleteVenueArea(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ENDPOINTS ESPECIALES PARA SEATS
    // ============================================

    /**
     * Obtener todos los asientos de un área
     */
    @GetMapping("/{areaId}/seats")
    public ResponseEntity<List<VenueSeatDTO>> getSeatsByAreaId(@PathVariable Long areaId) {
        List<VenueSeat> seats = venueAreaService.getSeatsByVenueAreaId(areaId);
        List<VenueSeatDTO> response = seats.stream()
                .map(seat -> modelMapper.map(seat, VenueSeatDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Generar asientos automáticamente para un área
     */
    @PostMapping("/{areaId}/seats/generate")
    public ResponseEntity<List<VenueSeatDTO>> generateSeats(
            @PathVariable Long areaId,
            @Valid @RequestBody GenerateSeatsRequestDTO request) {
        List<VenueSeat> seats = venueAreaService.generateSeats(areaId, request);
        List<VenueSeatDTO> response = seats.stream()
                .map(seat -> modelMapper.map(seat, VenueSeatDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

