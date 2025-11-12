package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.CreateEventRequest;
import com.event.app.dtos.EventDetailDTO;
import com.event.app.dtos.EventSummaryDTO;
import com.event.app.dtos.EventDTO;
import com.event.app.models.Event;

public interface IEventService {

    // Métodos principales
    Event createCompleteEvent(CreateEventRequest request);

    List<EventSummaryDTO> getAllEventsSummary();

    Optional<EventDetailDTO> getEventDetail(UUID id);

    // Métodos por organizer
    List<EventSummaryDTO> getEventsByOrganizerId(UUID organizerId);

    // Métodos de gestión
    Event updateEvent(UUID id, EventDTO eventDTO);

    void deleteEvent(UUID id);
}

