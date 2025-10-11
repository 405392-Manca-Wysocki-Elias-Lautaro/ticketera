package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.OrganizerDTO;
import com.event.app.models.Organizer;

public interface IOrganizerService {

    Organizer createOrganizer(OrganizerDTO organizerDTO);

    Optional<Organizer> getOrganizerById(UUID id);

    List<Organizer> getAllOrganizers();

    Organizer updateOrganizer(UUID id, OrganizerDTO organizerDTO);

    void deleteOrganizer(UUID id);
}
