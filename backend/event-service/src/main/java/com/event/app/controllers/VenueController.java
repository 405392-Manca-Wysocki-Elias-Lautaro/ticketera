package com.event.app.controllers;

import com.event.app.dtos.VenueDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Venue;
import com.event.app.services.IVenueService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
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
    public ResponseEntity<ApiResponse<VenueDTO>> createVenue(@Valid @RequestBody VenueDTO venueDTO) {
        Venue venue = venueService.createVenue(venueDTO);
        VenueDTO response = modelMapper.map(venue, VenueDTO.class);
        return ApiResponseFactory.created("Venue created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> getVenueById(@PathVariable UUID id) {
        return venueService.getVenueById(id)
                .map(venue -> ApiResponseFactory.success("Venue retrieved successfully", 
                        modelMapper.map(venue, VenueDTO.class)))
                .orElse(ApiResponseFactory.notFound("Venue not found with ID: " + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VenueDTO>>> getAllVenues() {
        List<VenueDTO> venues = venueService.getAllVenues().stream()
                .map(venue -> modelMapper.map(venue, VenueDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Venues retrieved successfully", venues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> updateVenue(@PathVariable UUID id, @Valid @RequestBody VenueDTO venueDTO) {
        Venue updated = venueService.updateVenue(id, venueDTO);
        VenueDTO response = modelMapper.map(updated, VenueDTO.class);
        return ApiResponseFactory.success("Venue updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVenue(@PathVariable UUID id) {
        venueService.deleteVenue(id);
        return ApiResponseFactory.success("Venue deleted successfully");
    }
}

