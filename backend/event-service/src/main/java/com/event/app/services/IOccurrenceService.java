package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.OccurrenceDTO;
import com.event.app.models.Occurrence;

public interface IOccurrenceService {

    Occurrence createOccurrence(OccurrenceDTO occurrenceDTO);

    Optional<Occurrence> getOccurrenceById(UUID id);

    List<Occurrence> getAllOccurrences();

    Occurrence updateOccurrence(UUID id, OccurrenceDTO occurrenceDTO);

    void deleteOccurrence(UUID id);
}

