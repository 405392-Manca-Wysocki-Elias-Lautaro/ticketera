package com.event.app.controllers;

import com.event.app.dtos.EventDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Event;
import com.event.app.services.IEventService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final IEventService eventService;
    private final ModelMapper modelMapper;

    public EventController(IEventService eventService, ModelMapper modelMapper) {
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        Event event = eventService.createEvent(eventDTO);
        EventDTO response = modelMapper.map(event, EventDTO.class);
        return ApiResponseFactory.created("Event created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id)
                .map(event -> ApiResponseFactory.success("Event retrieved successfully", 
                        modelMapper.map(event, EventDTO.class)))
                .orElse(ApiResponseFactory.notFound("Event not found with ID: " + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDTO>>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents().stream()
                .map(event -> modelMapper.map(event, EventDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Events retrieved successfully", events);
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

