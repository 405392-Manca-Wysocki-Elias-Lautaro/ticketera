package com.event.app.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.event.app.dtos.EventSummaryDTO;
import com.event.app.dtos.response.ApiResponse;
import com.event.app.services.IEventService;
import com.event.app.utils.ApiResponseFactory;
import com.event.app.exceptions.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/internal/events")
@Slf4j
public class EventInternalController {

    private final IEventService eventService;
    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    // TODO: Move to env var or config for better security
    private static final String INTERNAL_SECRET_VALUE = "TICKETERA_INTERNAL_SECRET_2024";

    public EventInternalController(IEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventSummaryDTO>>> getUpcomingEvents(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestHeader(value = INTERNAL_SECRET_HEADER, required = false) String secret) {
        
        // Simple security check for MVP
        if (!INTERNAL_SECRET_VALUE.equals(secret)) {
            log.warn("Unauthorized access attempt to internal endpoint");
            throw new UnauthorizedException("Invalid internal secret");
        }

        List<EventSummaryDTO> events = eventService.getEventsStartingBetween(start, end);
        return ApiResponseFactory.success("Upcoming events retrieved successfully", events);
    }
}
