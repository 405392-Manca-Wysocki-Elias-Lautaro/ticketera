package com.event.app.services;

import java.util.List;
import java.util.Optional;

import com.event.app.dtos.EventDTO;
import com.event.app.models.Event;

public interface IEventService {

    Event createEvent(EventDTO eventDTO);

    Optional<Event> getEventById(Long id);

    List<Event> getAllEvents();

    Event updateEvent(Long id, EventDTO eventDTO);

    void deleteEvent(Long id);
}

