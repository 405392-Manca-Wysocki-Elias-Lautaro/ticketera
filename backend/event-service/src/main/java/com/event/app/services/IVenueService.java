package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.VenueDTO;
import com.event.app.models.Venue;

public interface IVenueService {

    Venue createVenue(VenueDTO venueDTO);

    Optional<Venue> getVenueById(UUID id);

    List<Venue> getAllVenues();

    Venue updateVenue(UUID id, VenueDTO venueDTO);

    void deleteVenue(UUID id);
}

