package com.event.app.services;

import java.util.List;
import java.util.Optional;

import com.event.app.dtos.OrganizerDTO;
import com.event.app.models.Organizer;

public interface IOrganizerService {

    Organizer createOrganizer(OrganizerDTO organizerDTO);

    Optional<Organizer> getOrganizerById(Long id);

    List<Organizer> getAllOrganizers();

    Organizer updateOrganizer(Long id, OrganizerDTO organizerDTO);

    void deleteOrganizer(Long id);
}
