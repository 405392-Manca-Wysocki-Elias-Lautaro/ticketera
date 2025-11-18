package com.event.app.controllers;

import com.event.app.dtos.CreateEventRequest;
import com.event.app.dtos.EventDTO;
import com.event.app.dtos.EventDetailDTO;
import com.event.app.dtos.EventSummaryDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.exceptions.UnauthorizedException;
import com.event.app.models.Event;
import com.event.app.services.IEventService;
import com.event.app.utils.ApiResponseFactory;
import com.event.app.utils.JwtUtils;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class EventController {

    private final IEventService eventService;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    public EventController(IEventService eventService, ModelMapper modelMapper, JwtUtils jwtUtils) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
    }

    /**
     * POST /events - Crear evento completo con áreas, asientos y precios
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Event>> createCompleteEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = eventService.createCompleteEvent(request);
        return ApiResponseFactory.created("Evento completo creado exitosamente", event);
    }

    /**
     * GET /events - Obtener todos los eventos con información resumida enriquecida
     * Filtro opcional: title (String) - nombre del evento para búsqueda parcial
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventSummaryDTO>>> getAllEventsSummary(
            @RequestParam(required = false) String title) {
        List<EventSummaryDTO> events = eventService.getAllEvents(title);
        return ApiResponseFactory.success("Eventos obtenidos exitosamente", events);
    }

    /**
     * GET /events/{id} - Obtener detalle completo de un evento
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDetailDTO>> getEventDetail(@PathVariable UUID id) {
        return eventService.getEventDetail(id)
                .map(detail -> ApiResponseFactory.success("Detalle del evento obtenido exitosamente", detail))
                .orElse(ApiResponseFactory.notFound("Evento no encontrado con ID: " + id));
    }

    /**
     * GET /events/my-organization - Obtener eventos de mi organización (solo OWNER)
     */
    @GetMapping("/my-organization")
    public ResponseEntity<ApiResponse<List<EventSummaryDTO>>> getMyOrganizationEvents() {
        // Verificar que sea OWNER
        if (!jwtUtils.isOwner()) {
            throw new UnauthorizedException("Solo los OWNER pueden acceder a esta funcionalidad");
        }

        // Obtener el organizerId del JWT
        UUID organizerId = jwtUtils.getOrganizerId();
        
        // Obtener eventos del organizer
        List<EventSummaryDTO> events = eventService.getEventsByOrganizerId(organizerId);
        
        return ApiResponseFactory.success("Eventos de la organización obtenidos exitosamente", events);
    }

    /**
     * GET /events/staff - Obtener eventos asignados al usuario (STAFF u OWNER)
     */
    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<List<EventSummaryDTO>>> getEventsForStaff() {
        // Verificar que sea STAFF u OWNER
        if (!jwtUtils.isStaff()) {
            throw new UnauthorizedException("Solo el personal autorizado puede acceder a esta funcionalidad");
        }

        // Obtener el organizerId del JWT
        UUID organizerId = jwtUtils.getOrganizerId();
        
        // Obtener eventos del organizador asignado al usuario
        List<EventSummaryDTO> events = eventService.getEventsByOrganizerId(organizerId);
        
        return ApiResponseFactory.success("Eventos obtenidos exitosamente", events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(@PathVariable UUID id, @Valid @RequestBody EventDTO eventDTO) {
        Event updated = eventService.updateEvent(id, eventDTO);
        EventDTO response = modelMapper.map(updated, EventDTO.class);
        return ApiResponseFactory.success("Event updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ApiResponseFactory.success("Event deleted successfully");
    }
}

