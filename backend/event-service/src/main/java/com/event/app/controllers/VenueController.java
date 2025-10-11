package com.event.app.controllers;

import com.event.app.dtos.VenueDTO;
import com.event.app.models.Venue;
import com.event.app.services.IVenueService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final IVenueService venueService;
    private final ModelMapper modelMapper;

    public VenueController(IVenueService venueService, ModelMapper modelMapper) {
        this.venueService = venueService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<VenueDTO> createVenue(@Valid @RequestBody VenueDTO venueDTO) {
        Venue venue = venueService.createVenue(venueDTO);
        VenueDTO response = modelMapper.map(venue, VenueDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDTO> getVenueById(@PathVariable Long id) {
        return venueService.getVenueById(id)
                .map(venue -> ResponseEntity.ok(modelMapper.map(venue, VenueDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<VenueDTO>> getAllVenues() {
        List<VenueDTO> venues = venueService.getAllVenues().stream()
                .map(venue -> modelMapper.map(venue, VenueDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(venues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueDTO> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueDTO venueDTO) {
        Venue updated = venueService.updateVenue(id, venueDTO);
        VenueDTO response = modelMapper.map(updated, VenueDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}

