package com.event.app.services.impl;

import com.event.app.dtos.OccurrenceDTO;
import com.event.app.models.Occurrence;
import com.event.app.entities.OccurrenceEntity;
import com.event.app.exceptions.OccurrenceNotFoundException;
import com.event.app.repositories.OccurrenceRepository;
import com.event.app.services.IOccurrenceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OccurrenceServiceImpl implements IOccurrenceService {

    private final OccurrenceRepository eventOccurrenceRepository;
    private final ModelMapper modelMapper;

    public OccurrenceServiceImpl(OccurrenceRepository eventOccurrenceRepository, ModelMapper modelMapper) {
        this.eventOccurrenceRepository = eventOccurrenceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Occurrence createOccurrence(OccurrenceDTO eventOccurrenceDTO) {
        Occurrence eventOccurrence = modelMapper.map(eventOccurrenceDTO, Occurrence.class);

        eventOccurrence.setActive(true);
        OccurrenceEntity entity = modelMapper.map(eventOccurrence, OccurrenceEntity.class);
        OccurrenceEntity saved = eventOccurrenceRepository.save(entity);

        return modelMapper.map(saved, Occurrence.class);
    }

    @Override
    public Optional<Occurrence> getOccurrenceById(UUID id) {
        return eventOccurrenceRepository.findById(id)
                .filter(OccurrenceEntity::getActive)
                .map(entity -> modelMapper.map(entity, Occurrence.class));
    }

    @Override
    public List<Occurrence> getAllOccurrences() {
        return eventOccurrenceRepository.findByActiveTrue().stream()
                .map(entity -> modelMapper.map(entity, Occurrence.class))
                .collect(Collectors.toList());
    }

    @Override
    public Occurrence updateOccurrence(UUID id, OccurrenceDTO eventOccurrenceDTO) {
        OccurrenceEntity eventOccurrenceEntity = eventOccurrenceRepository.findById(id)
            .filter(OccurrenceEntity::getActive)
            .orElseThrow(() -> new OccurrenceNotFoundException(id));

        eventOccurrenceEntity.setEventId(eventOccurrenceDTO.getEventId());
        eventOccurrenceEntity.setVenueId(eventOccurrenceDTO.getVenueId());
        eventOccurrenceEntity.setStartsAt(eventOccurrenceDTO.getStartsAt());
        eventOccurrenceEntity.setEndsAt(eventOccurrenceDTO.getEndsAt());
        eventOccurrenceEntity.setStatus(eventOccurrenceDTO.getStatus());
        eventOccurrenceEntity.setSlug(eventOccurrenceDTO.getSlug());

        OccurrenceEntity eventOccurrenceUpdated = eventOccurrenceRepository.save(eventOccurrenceEntity);
        return modelMapper.map(eventOccurrenceUpdated, Occurrence.class);
    }

    @Override
    public void deleteOccurrence(UUID id) {
        OccurrenceEntity eventOccurrenceEntity = eventOccurrenceRepository.findById(id)
            .filter(OccurrenceEntity::getActive)
            .orElseThrow(() -> new OccurrenceNotFoundException(id));

        eventOccurrenceEntity.setActive(false); 
        eventOccurrenceRepository.save(eventOccurrenceEntity);
    }
}

