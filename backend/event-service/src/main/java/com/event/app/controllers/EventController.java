package com.event.app.controllers;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.event.app.dtos.CreateEventRequest;
import com.event.app.dtos.EventDTO;
import com.event.app.dtos.EventDetailDTO;
import com.event.app.dtos.EventSummaryDTO;
import com.event.app.dtos.OrganizerMetricsDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.exceptions.UnauthorizedException;
import com.event.app.models.Event;
import com.event.app.services.IEventService;
import com.event.app.services.IMetricsService;
import com.event.app.utils.ApiResponseFactory;
import com.event.app.utils.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/")
public class EventController {

    private final IEventService eventService;
    private final IMetricsService metricsService;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    public EventController(IEventService eventService, IMetricsService metricsService, ModelMapper modelMapper, JwtUtils jwtUtils) {
        this.eventService = eventService;
        this.metricsService = metricsService;
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
        // Verificar que sea OWNER o ADMIN
        if (!jwtUtils.isOwner() && !jwtUtils.isAdmin()) {
            throw new UnauthorizedException("Solo los OWNER y ADMIN pueden acceder a esta funcionalidad");
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

    /**
     * GET /events/metrics - Obtener métricas del organizador (OWNER, ADMIN o SUPER_ADMIN)
     */
    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<OrganizerMetricsDTO>> getOrganizerMetrics() {
        // Verificar que sea OWNER, ADMIN o SUPER_ADMIN
        String role = jwtUtils.getRole();
        if (!"OWNER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role) && !"SUPER_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Solo los OWNER, ADMIN o SUPER_ADMIN pueden acceder a esta funcionalidad");
        }

        UUID organizerId = jwtUtils.getOrganizerId();
        
        OrganizerMetricsDTO metrics = metricsService.getOrganizerMetrics(organizerId);
        
        return ApiResponseFactory.success("Métricas obtenidas exitosamente", metrics);
    }

    @PostMapping("/{id}/staff")
    public ResponseEntity<ApiResponse<Void>> assignStaff(@PathVariable UUID id, @RequestBody java.util.Map<String, UUID> request) {
        if (!jwtUtils.isOwner() && !"ADMIN".equalsIgnoreCase(jwtUtils.getRole())) {
             throw new UnauthorizedException("Only owners or admins can assign staff");
        }

        UUID userId = request.get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        
        UUID assignedBy = jwtUtils.getUserId();
        
        eventService.assignStaff(id, userId, assignedBy);
        
        return ApiResponseFactory.success("Staff assigned successfully");
    }

    @DeleteMapping("/{id}/staff/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeStaff(@PathVariable UUID id, @PathVariable UUID userId) {
        if (!jwtUtils.isOwner() && !"ADMIN".equalsIgnoreCase(jwtUtils.getRole())) {
             throw new UnauthorizedException("Only owners or admins can remove staff");
        }

        eventService.removeStaff(id, userId);
        return ApiResponseFactory.success("Staff removed successfully");
    }

    @GetMapping("/{id}/staff")
    public ResponseEntity<ApiResponse<List<UUID>>> getEventStaff(@PathVariable UUID id) {
        List<UUID> staffIds = eventService.getEventStaffUserIds(id);
        return ApiResponseFactory.success("Event staff retrieved successfully", staffIds);
    }
}

