package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.EventDTO;
import com.event.app.models.Event;

public interface IEventService {

    Event createEvent(EventDTO eventDTO);

    Optional<Event> getEventById(UUID id);

    List<Event> getAllEvents();

    Event updateEvent(UUID id, EventDTO eventDTO);

    void deleteEvent(UUID id);
}

