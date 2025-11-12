package com.event.app.controllers;

import com.event.app.dtos.SeatDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.models.Seat;
import com.event.app.services.ISeatService;
import com.event.app.utils.ApiResponseFactory;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seats")
public class SeatController {

    private final ISeatService seatService;
    private final ModelMapper modelMapper;

    public SeatController(ISeatService seatService, ModelMapper modelMapper) {
        this.seatService = seatService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeatDTO>> createSeat(@Valid @RequestBody SeatDTO seatDTO) {
        Seat seat = seatService.createSeat(seatDTO);
        SeatDTO response = modelMapper.map(seat, SeatDTO.class);
        return ApiResponseFactory.created("Seat created successfully", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatDTO>> getSeatById(@PathVariable UUID id) {
        return seatService.getSeatById(id)
                .map(seat -> ApiResponseFactory.success("Seat retrieved successfully", 
                        modelMapper.map(seat, SeatDTO.class)))
                .orElse(ApiResponseFactory.notFound("Seat not found with ID: " + id));
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getSeatsByAreaId(@PathVariable UUID areaId) {
        List<SeatDTO> seats = seatService.getSeatsByAreaId(areaId).stream()
                .map(seat -> modelMapper.map(seat, SeatDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Seats retrieved successfully", seats);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatDTO>>> getAllSeats() {
        List<SeatDTO> seats = seatService.getAllSeats().stream()
                .map(seat -> modelMapper.map(seat, SeatDTO.class))
                .collect(Collectors.toList());
        return ApiResponseFactory.success("Seats retrieved successfully", seats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatDTO>> updateSeat(@PathVariable UUID id, @Valid @RequestBody SeatDTO seatDTO) {
        Seat updated = seatService.updateSeat(id, seatDTO);
        SeatDTO response = modelMapper.map(updated, SeatDTO.class);
        return ApiResponseFactory.success("Seat updated successfully", response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(@PathVariable UUID id) {
        seatService.deleteSeat(id);
        return ApiResponseFactory.success("Seat deleted successfully");
    }
}

