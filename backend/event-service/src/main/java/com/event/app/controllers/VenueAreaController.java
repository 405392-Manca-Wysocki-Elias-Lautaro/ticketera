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
@RequestMapping("/venue/{venueId}/areas")
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
    public ResponseEntity<VenueAreaDTO> createVenueArea(
            @PathVariable UUID venueId,
            @Valid @RequestBody VenueAreaDTO venueAreaDTO) {
        venueAreaDTO.setVenueId(venueId);
        VenueArea venueArea = venueAreaService.createVenueArea(venueAreaDTO);
        VenueAreaDTO response = modelMapper.map(venueArea, VenueAreaDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtener un área de venue por ID
     */
    @GetMapping("/{areaId}")
    public ResponseEntity<VenueAreaDTO> getVenueAreaById(
            @PathVariable UUID venueId,
            @PathVariable UUID areaId) {
        return venueAreaService.getVenueAreaById(areaId)
                .map(venueArea -> ResponseEntity.ok(modelMapper.map(venueArea, VenueAreaDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener todas las áreas de un venue específico
     */
    @GetMapping
    public ResponseEntity<List<VenueAreaDTO>> getAllAreasByVenueId(
            @PathVariable UUID venueId) {
        
        List<VenueArea> venueAreas = venueAreaService.getVenueAreasByVenueId(venueId);
        
        List<VenueAreaDTO> response = venueAreas.stream()
                .map(venueArea -> modelMapper.map(venueArea, VenueAreaDTO.class))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar un área de venue
     */
    @PutMapping("/{areaId}")
    public ResponseEntity<VenueAreaDTO> updateVenueArea(
            @PathVariable UUID venueId,
            @PathVariable UUID areaId,
            @Valid @RequestBody VenueAreaDTO venueAreaDTO) {
        // Asegurar que el venueId del path coincida con el del body
        venueAreaDTO.setVenueId(venueId);
        VenueArea updated = venueAreaService.updateVenueArea(areaId, venueAreaDTO);
        VenueAreaDTO response = modelMapper.map(updated, VenueAreaDTO.class);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un área de venue
     */
    @DeleteMapping("/{areaId}")
    public ResponseEntity<Void> deleteVenueArea(
            @PathVariable UUID venueId,
            @PathVariable UUID areaId) {
        venueAreaService.deleteVenueArea(areaId);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // ENDPOINTS ESPECIALES PARA SEATS
    // ============================================

    /**
     * Obtener todos los asientos de un área
     */
    @GetMapping("/{areaId}/seats")
    public ResponseEntity<List<VenueSeatDTO>> getSeatsByAreaId(
            @PathVariable UUID venueId,
            @PathVariable UUID areaId) {
        List<VenueSeat> seats = venueAreaService.getSeatsByVenueAreaId(areaId);
        List<VenueSeatDTO> response = seats.stream()
                .map(seat -> modelMapper.map(seat, VenueSeatDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Generar asientos automáticamente para un área
     * 
     * Este endpoint permite crear asientos de forma flexible mediante un arreglo de configuraciones de filas.
     * Cada fila puede tener diferente cantidad de asientos y número inicial personalizado.
     * Los labels de las filas se generan automáticamente como "Fila 1", "Fila 2", etc.
     * 
     * Ejemplo de request body:
     * {
     *   "rows": [
     *     {
     *       "seatsPerRow": 10
     *     },
     *     {
     *       "seatsPerRow": 15
     *     },
     *     {
     *       "seatsPerRow": 12
     *     }
     *   ]
     * }
     * 
     * Resultado:
     * - Fila 1: 11, 12, 13, ..., 110 (rowNumber=1, seatNumber=1-10, label="11"-"110")
     * - Fila 2: 21, 22, 23, ..., 215 (rowNumber=2, seatNumber=1-15, label="21"-"215")  
     * - Fila 3: 31, 32, 33, ..., 312 (rowNumber=3, seatNumber=1-12, label="31"-"312")
     * 
     * @param venueId ID del venue
     * @param areaId ID del área donde generar los asientos
     * @param request Configuración de filas con sus respectivos asientos
     * @return Lista de asientos generados
     */
    @PostMapping("/{areaId}/seats/generate")
    public ResponseEntity<List<VenueSeatDTO>> generateSeats(
            @PathVariable UUID venueId,
            @PathVariable UUID areaId,
            @Valid @RequestBody GenerateSeatsRequestDTO request) {
        List<VenueSeat> seats = venueAreaService.generateSeats(areaId, request);
        List<VenueSeatDTO> response = seats.stream()
                .map(seat -> modelMapper.map(seat, VenueSeatDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

