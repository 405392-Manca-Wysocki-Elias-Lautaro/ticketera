package com.event.app.services;

import java.util.List;
import java.util.Optional;

import com.event.app.dtos.VenueDTO;
import com.event.app.models.Venue;

public interface IVenueService {

    Venue createVenue(VenueDTO venueDTO);

    Optional<Venue> getVenueById(Long id);

    List<Venue> getAllVenues();

    Venue updateVenue(Long id, VenueDTO venueDTO);

    void deleteVenue(Long id);
}

