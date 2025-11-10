package com.event.app.controllers;

import com.event.app.dtos.OrganizerDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Organizer;
import com.event.app.services.IOrganizerService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
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
    public ResponseEntity<ApiResponse<OrganizerDTO>> createOrganizer(@Valid @RequestBody OrganizerDTO organizerDTO) {
        Organizer organizer = organizerService.createOrganizer(organizerDTO);
        OrganizerDTO response = modelMapper.map(organizer, OrganizerDTO.class);
        return ApiResponseFactory.created("Organizer created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizerDTO>> getOrganizerById(@PathVariable UUID id) {
        return organizerService.getOrganizerById(id)
                .map(organizer -> ApiResponseFactory.success("Organizer retrieved successfully", 
                        modelMapper.map(organizer, OrganizerDTO.class)))
                .orElse(ApiResponseFactory.notFound("Organizer not found with ID: " + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizerDTO>>> getAllOrganizers() {
        List<OrganizerDTO> organizers = organizerService.getAllOrganizers().stream()
                .map(organizer -> modelMapper.map(organizer, OrganizerDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Organizers retrieved successfully", organizers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizerDTO>> updateOrganizer(@PathVariable UUID id, @Valid @RequestBody OrganizerDTO organizerDTO) {
        Organizer updated = organizerService.updateOrganizer(id, organizerDTO);
        OrganizerDTO response = modelMapper.map(updated, OrganizerDTO.class);
        return ApiResponseFactory.success("Organizer updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganizer(@PathVariable UUID id) {
        organizerService.deleteOrganizer(id);
        return ApiResponseFactory.success("Organizer deleted successfully");
    }
}
