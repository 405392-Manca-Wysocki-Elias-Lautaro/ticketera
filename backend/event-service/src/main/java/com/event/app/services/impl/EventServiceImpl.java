package com.event.app.services.impl;

import com.event.app.dtos.EventDTO;
import com.event.app.models.Event;
import com.event.app.entities.EventEntity;
import com.event.app.exceptions.EventNotFoundException;
import com.event.app.repositories.EventRepository;
import com.event.app.services.IEventService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventServiceImpl(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Event createEvent(EventDTO eventDTO) {
        Event event = modelMapper.map(eventDTO, Event.class);

        event.setCreatedAt(LocalDateTime.now());
        event.setActive(true);
        EventEntity entity = modelMapper.map(event, EventEntity.class);
        EventEntity saved = eventRepository.save(entity);

        return modelMapper.map(saved, Event.class);
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id)
                .filter(EventEntity::getActive)
                .map(entity -> modelMapper.map(entity, Event.class));
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findByActiveTrue().stream()
                .map(entity -> modelMapper.map(entity, Event.class))
                .collect(Collectors.toList());
    }

    @Override
    public Event updateEvent(Long id, EventDTO eventDTO) {
        EventEntity eventEntity = eventRepository.findById(id)
            .filter(EventEntity::getActive)
            .orElseThrow(() -> new EventNotFoundException(id));

        eventEntity.setOrganizerId(eventDTO.getOrganizerId());
        eventEntity.setTitle(eventDTO.getTitle());
        eventEntity.setSlug(eventDTO.getSlug());
        eventEntity.setDescription(eventDTO.getDescription());
        eventEntity.setCategoryId(eventDTO.getCategoryId());
        eventEntity.setCoverUrl(eventDTO.getCoverUrl());
        eventEntity.setStatus(eventDTO.getStatus());

        EventEntity eventUpdated = eventRepository.save(eventEntity);
        return modelMapper.map(eventUpdated, Event.class);
    }

    @Override
    public void deleteEvent(Long id) {
        EventEntity eventEntity = eventRepository.findById(id)
            .filter(EventEntity::getActive)
            .orElseThrow(() -> new EventNotFoundException(id));

        eventEntity.setActive(false); 
        eventRepository.save(eventEntity);
    }
}

