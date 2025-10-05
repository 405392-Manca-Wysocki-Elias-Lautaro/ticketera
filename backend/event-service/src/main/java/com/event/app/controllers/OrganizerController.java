package com.event.app.controllers;

import com.event.app.dtos.OrganizerDTO;
import com.event.app.models.Organizer;
import com.event.app.services.IOrganizerService;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/organizers")
public class OrganizerController {

    private final IOrganizerService organizerService;
    private final ModelMapper modelMapper;

    public OrganizerController(IOrganizerService organizerService, ModelMapper modelMapper) {
        this.organizerService = organizerService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<OrganizerDTO> createOrganizer(@Valid @RequestBody OrganizerDTO organizerDTO) {
        Organizer organizer = organizerService.createOrganizer(organizerDTO);
        OrganizerDTO response = modelMapper.map(organizer, OrganizerDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizerDTO> getOrganizerById(@PathVariable Long id) {
        return organizerService.getOrganizerById(id)
                .map(organizer -> ResponseEntity.ok(modelMapper.map(organizer, OrganizerDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrganizerDTO>> getAllOrganizers() {
        List<OrganizerDTO> organizers = organizerService.getAllOrganizers().stream()
                .map(organizer -> modelMapper.map(organizer, OrganizerDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizerDTO> updateOrganizer(@PathVariable Long id, @Valid @RequestBody OrganizerDTO organizerDTO) {
        Organizer updated = organizerService.updateOrganizer(id, organizerDTO);
        OrganizerDTO response = modelMapper.map(updated, OrganizerDTO.class);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
        organizerService.deleteOrganizer(id);
        return ResponseEntity.noContent().build();
    }
}
