package com.event.app.controllers;

import com.event.app.dtos.OccurrenceDTO;
import com.event.app.models.Occurrence;
import com.event.app.services.IOccurrenceService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<OccurrenceDTO> createOccurrence(@Valid @RequestBody OccurrenceDTO eventOccurrenceDTO) {
        Occurrence eventOccurrence = eventOccurrenceService.createOccurrence(eventOccurrenceDTO);
        OccurrenceDTO response = modelMapper.map(eventOccurrence, OccurrenceDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OccurrenceDTO> getOccurrenceById(@PathVariable UUID id) {
        return eventOccurrenceService.getOccurrenceById(id)
                .map(eventOccurrence -> ResponseEntity.ok(modelMapper.map(eventOccurrence, OccurrenceDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OccurrenceDTO>> getAllOccurrences() {
        List<OccurrenceDTO> eventOccurrences = eventOccurrenceService.getAllOccurrences().stream()
                .map(eventOccurrence -> modelMapper.map(eventOccurrence, OccurrenceDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventOccurrences);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OccurrenceDTO> updateOccurrence(@PathVariable UUID id, @Valid @RequestBody OccurrenceDTO eventOccurrenceDTO) {
        Occurrence updated = eventOccurrenceService.updateOccurrence(id, eventOccurrenceDTO);
        OccurrenceDTO response = modelMapper.map(updated, OccurrenceDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccurrence(@PathVariable UUID id) {
        eventOccurrenceService.deleteOccurrence(id);
        return ResponseEntity.noContent().build();
    }
}

