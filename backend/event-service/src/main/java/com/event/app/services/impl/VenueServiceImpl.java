package com.event.app.services.impl;

import com.event.app.dtos.VenueDTO;
import com.event.app.models.Venue;
import com.event.app.entities.VenueEntity;
import com.event.app.exceptions.VenueNotFoundException;
import com.event.app.repositories.VenueRepository;
import com.event.app.services.IVenueService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl implements IVenueService {

    private final VenueRepository venueRepository;
    private final ModelMapper modelMapper;

    public VenueServiceImpl(VenueRepository venueRepository, ModelMapper modelMapper) {
        this.venueRepository = venueRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Venue createVenue(VenueDTO venueDTO) {
        Venue venue = modelMapper.map(venueDTO, Venue.class);

        venue.setCreatedAt(LocalDateTime.now());
        venue.setActive(true);
        VenueEntity entity = modelMapper.map(venue, VenueEntity.class);
        VenueEntity saved = venueRepository.save(entity);

        return modelMapper.map(saved, Venue.class);
    }

    @Override
    public Optional<Venue> getVenueById(UUID id) {
        return venueRepository.findById(id)
                .filter(VenueEntity::getActive)
                .map(entity -> modelMapper.map(entity, Venue.class));
    }

    @Override
    public List<Venue> getAllVenues() {
        return venueRepository.findByActiveTrue().stream()
                .map(entity -> modelMapper.map(entity, Venue.class))
                .collect(Collectors.toList());
    }

    @Override
    public Venue updateVenue(UUID id, VenueDTO venueDTO) {
        VenueEntity venueEntity = venueRepository.findById(id)
            .filter(VenueEntity::getActive)
            .orElseThrow(() -> new VenueNotFoundException(id));

        venueEntity.setOrganizerId(venueDTO.getOrganizerId());
        venueEntity.setName(venueDTO.getName());
        venueEntity.setDescription(venueDTO.getDescription());
        venueEntity.setAddressLine(venueDTO.getAddressLine());
        venueEntity.setCity(venueDTO.getCity());
        venueEntity.setState(venueDTO.getState());
        venueEntity.setCountry(venueDTO.getCountry());
        venueEntity.setLat(venueDTO.getLat());
        venueEntity.setLng(venueDTO.getLng());

        VenueEntity venueUpdated = venueRepository.save(venueEntity);
        return modelMapper.map(venueUpdated, Venue.class);
    }

    @Override
    public void deleteVenue(UUID id) {
        VenueEntity venueEntity = venueRepository.findById(id)
            .filter(VenueEntity::getActive)
            .orElseThrow(() -> new VenueNotFoundException(id));

        venueEntity.setActive(false); 
        venueRepository.save(venueEntity);
    }
}

