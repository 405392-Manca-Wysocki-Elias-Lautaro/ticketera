package com.event.app.controllers;

import com.event.app.dtos.AreaDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Area;
import com.event.app.services.IAreaService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/areas")
public class AreaController {

    private final IAreaService areaService;
    private final ModelMapper modelMapper;

    public AreaController(IAreaService areaService, ModelMapper modelMapper) {
        this.areaService = areaService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AreaDTO>> createArea(@Valid @RequestBody AreaDTO areaDTO) {
        Area area = areaService.createArea(areaDTO);
        AreaDTO response = modelMapper.map(area, AreaDTO.class);
        return ApiResponseFactory.created("Area created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AreaDTO>> getAreaById(@PathVariable UUID id) {
        return areaService.getAreaById(id)
                .map(area -> ApiResponseFactory.success("Area retrieved successfully", 
                        modelMapper.map(area, AreaDTO.class)))
                .orElse(ApiResponseFactory.notFound("Area not found with ID: " + id));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<AreaDTO>>> getAreasByEventId(@PathVariable UUID eventId) {
        List<AreaDTO> areas = areaService.getAreasByEventId(eventId).stream()
                .map(area -> modelMapper.map(area, AreaDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Areas retrieved successfully", areas);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AreaDTO>>> getAllAreas() {
        List<AreaDTO> areas = areaService.getAllAreas().stream()
                .map(area -> modelMapper.map(area, AreaDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Areas retrieved successfully", areas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AreaDTO>> updateArea(@PathVariable UUID id, @Valid @RequestBody AreaDTO areaDTO) {
        Area updated = areaService.updateArea(id, areaDTO);
        AreaDTO response = modelMapper.map(updated, AreaDTO.class);
        return ApiResponseFactory.success("Area updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArea(@PathVariable UUID id) {
        areaService.deleteArea(id);
        return ApiResponseFactory.success("Area deleted successfully");
    }
}

