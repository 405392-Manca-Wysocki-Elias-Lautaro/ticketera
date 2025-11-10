package com.event.app.controllers;

import com.event.app.dtos.OccurrenceDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Occurrence;
import com.event.app.services.IOccurrenceService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/occurrences")
public class OccurrenceController {

    private final IOccurrenceService eventOccurrenceService;
    private final ModelMapper modelMapper;

    public OccurrenceController(IOccurrenceService eventOccurrenceService, ModelMapper modelMapper) {
        this.eventOccurrenceService = eventOccurrenceService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OccurrenceDTO>> createOccurrence(@Valid @RequestBody OccurrenceDTO eventOccurrenceDTO) {
        Occurrence eventOccurrence = eventOccurrenceService.createOccurrence(eventOccurrenceDTO);
        OccurrenceDTO response = modelMapper.map(eventOccurrence, OccurrenceDTO.class);
        return ApiResponseFactory.created("Occurrence created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OccurrenceDTO>> getOccurrenceById(@PathVariable UUID id) {
        return eventOccurrenceService.getOccurrenceById(id)
                .map(eventOccurrence -> ApiResponseFactory.success("Occurrence retrieved successfully", 
                        modelMapper.map(eventOccurrence, OccurrenceDTO.class)))
                .orElse(ApiResponseFactory.notFound("Occurrence not found with ID: " + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OccurrenceDTO>>> getAllOccurrences() {
        List<OccurrenceDTO> eventOccurrences = eventOccurrenceService.getAllOccurrences().stream()
                .map(eventOccurrence -> modelMapper.map(eventOccurrence, OccurrenceDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Occurrences retrieved successfully", eventOccurrences);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OccurrenceDTO>> updateOccurrence(@PathVariable UUID id, @Valid @RequestBody OccurrenceDTO eventOccurrenceDTO) {
        Occurrence updated = eventOccurrenceService.updateOccurrence(id, eventOccurrenceDTO);
        OccurrenceDTO response = modelMapper.map(updated, OccurrenceDTO.class);
        return ApiResponseFactory.success("Occurrence updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOccurrence(@PathVariable UUID id) {
        eventOccurrenceService.deleteOccurrence(id);
        return ApiResponseFactory.success("Occurrence deleted successfully");
    }
}

