package com.event.app.services.impl;

import com.event.app.dtos.CreateEventRequest;
import com.event.app.dtos.EventDTO;
import com.event.app.dtos.EventDetailDTO;
import com.event.app.dtos.EventSummaryDTO;
import com.event.app.entities.AreaEntity;
import com.event.app.entities.AreaPricingEntity;
import com.event.app.entities.SeatEntity;
import com.event.app.models.Event;
import com.event.app.entities.EventEntity;
import com.event.app.exceptions.EventNotFoundException;
import com.event.app.repositories.AreaPricingRepository;
import com.event.app.repositories.AreaRepository;
import com.event.app.repositories.CategoryRepository;
import com.event.app.repositories.EventRepository;
import com.event.app.repositories.SeatRepository;
import com.event.app.services.IAvailabilityService;
import com.event.app.services.IEventService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private final AreaRepository areaRepository;
    private final SeatRepository seatRepository;
    private final AreaPricingRepository areaPricingRepository;
    private final CategoryRepository categoryRepository;
    private final IAvailabilityService availabilityService;
    private final ModelMapper modelMapper;

    public EventServiceImpl(
            EventRepository eventRepository,
            AreaRepository areaRepository,
            SeatRepository seatRepository,
            AreaPricingRepository areaPricingRepository,
            CategoryRepository categoryRepository,
            IAvailabilityService availabilityService,
            ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.areaRepository = areaRepository;
        this.seatRepository = seatRepository;
        this.areaPricingRepository = areaPricingRepository;
        this.categoryRepository = categoryRepository;
        this.availabilityService = availabilityService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Event updateEvent(UUID id, EventDTO eventDTO) {
        EventEntity eventEntity = eventRepository.findById(id)
            .filter(EventEntity::getActive)
            .orElseThrow(() -> new EventNotFoundException(id));

        eventEntity.setOrganizerId(eventDTO.getOrganizerId());
        eventEntity.setTitle(eventDTO.getTitle());
        eventEntity.setSlug(eventDTO.getSlug());
        eventEntity.setDescription(eventDTO.getDescription());
        eventEntity.setCategoryId(eventDTO.getCategoryId());
        eventEntity.setCoverUrl(eventDTO.getCoverUrl());
        eventEntity.setStatus(eventDTO.getStatus());
        eventEntity.setVenueName(eventDTO.getVenueName());
        eventEntity.setVenueDescription(eventDTO.getVenueDescription());
        eventEntity.setAddressLine(eventDTO.getAddressLine());
        eventEntity.setCity(eventDTO.getCity());
        eventEntity.setState(eventDTO.getState());
        eventEntity.setCountry(eventDTO.getCountry());
        eventEntity.setLat(eventDTO.getLat());
        eventEntity.setLng(eventDTO.getLng());
        eventEntity.setStartsAt(eventDTO.getStartsAt());
        eventEntity.setEndsAt(eventDTO.getEndsAt());

        EventEntity eventUpdated = eventRepository.save(eventEntity);
        return modelMapper.map(eventUpdated, Event.class);
    }

    @Override
    public void deleteEvent(UUID id) {
        EventEntity eventEntity = eventRepository.findById(id)
            .filter(EventEntity::getActive)
            .orElseThrow(() -> new EventNotFoundException(id));

        eventEntity.setActive(false); 
        eventRepository.save(eventEntity);
    }

    @Override
    @Transactional
    public Event createCompleteEvent(CreateEventRequest request) {
        // 1. Crear el evento
        EventEntity eventEntity = new EventEntity();
        eventEntity.setOrganizerId(request.getOrganizerId());
        eventEntity.setTitle(request.getTitle());
        eventEntity.setSlug(request.getSlug());
        eventEntity.setDescription(request.getDescription());
        eventEntity.setCategoryId(request.getCategoryId());
        eventEntity.setCoverUrl(request.getCoverUrl());
        eventEntity.setStatus(request.getStatus());
        eventEntity.setVenueName(request.getVenueName());
        eventEntity.setVenueDescription(request.getVenueDescription());
        eventEntity.setAddressLine(request.getAddressLine());
        eventEntity.setCity(request.getCity());
        eventEntity.setState(request.getState());
        eventEntity.setCountry(request.getCountry());
        eventEntity.setLat(request.getLat());
        eventEntity.setLng(request.getLng());
        eventEntity.setStartsAt(request.getStartsAt());
        eventEntity.setEndsAt(request.getEndsAt());
        eventEntity.setCreatedAt(LocalDateTime.now());
        eventEntity.setActive(true);

        EventEntity savedEvent = eventRepository.save(eventEntity);

        // 2. Crear áreas, asientos y precios
        if (request.getAreas() != null && !request.getAreas().isEmpty()) {
            for (CreateEventRequest.CreateAreaRequest areaRequest : request.getAreas()) {
                // Crear área
                AreaEntity areaEntity = new AreaEntity();
                areaEntity.setEventId(savedEvent.getId());
                areaEntity.setName(areaRequest.getName());
                areaEntity.setIsGeneralAdmission(areaRequest.getIsGeneralAdmission());
                areaEntity.setCapacity(areaRequest.getCapacity());
                areaEntity.setPosition(areaRequest.getPosition());

                AreaEntity savedArea = areaRepository.save(areaEntity);

                // Crear precio para el área
                AreaPricingEntity pricingEntity = new AreaPricingEntity();
                pricingEntity.setAreaId(savedArea.getId());
                pricingEntity.setPriceCents(areaRequest.getPriceCents());
                pricingEntity.setCurrency(areaRequest.getCurrency() != null ? areaRequest.getCurrency() : "ARS");
                pricingEntity.setCreatedAt(LocalDateTime.now());
                areaPricingRepository.save(pricingEntity);

                // Crear asientos si existen
                if (areaRequest.getSeats() != null && !areaRequest.getSeats().isEmpty()) {
                    for (CreateEventRequest.CreateSeatRequest seatRequest : areaRequest.getSeats()) {
                        SeatEntity seatEntity = new SeatEntity();
                        seatEntity.setAreaId(savedArea.getId());
                        seatEntity.setSeatNumber(seatRequest.getSeatNumber());
                        seatEntity.setRowNumber(seatRequest.getRowNumber());
                        seatEntity.setLabel(seatRequest.getLabel());
                        seatRepository.save(seatEntity);
                    }
                }
            }
        }

        return modelMapper.map(savedEvent, Event.class);
    }

    @Override
    public List<EventSummaryDTO> getAllEventsSummary() {
        List<EventEntity> events = eventRepository.findByActiveTrue();
        
        return events.stream()
                .map(this::mapToEventSummary)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EventDetailDTO> getEventDetail(UUID id) {
        return eventRepository.findById(id)
                .filter(EventEntity::getActive)
                .map(this::mapToEventDetail);
    }

    @Override
    public List<EventSummaryDTO> getEventsByOrganizerId(UUID organizerId) {
        List<EventEntity> events = eventRepository.findByOrganizerIdAndActiveTrue(organizerId);
        
        return events.stream()
                .map(this::mapToEventSummary)
                .collect(Collectors.toList());
    }

    private EventSummaryDTO mapToEventSummary(EventEntity event) {
        EventSummaryDTO dto = EventSummaryDTO.builder()
                .id(event.getId())
                .organizerId(event.getOrganizerId())
                .title(event.getTitle())
                .slug(event.getSlug())
                .description(event.getDescription())
                .coverUrl(event.getCoverUrl())
                .status(event.getStatus())
                .venueName(event.getVenueName())
                .addressLine(event.getAddressLine())
                .city(event.getCity())
                .state(event.getState())
                .country(event.getCountry())
                .lat(event.getLat())
                .lng(event.getLng())
                .startsAt(event.getStartsAt())
                .endsAt(event.getEndsAt())
                .build();

        // Obtener categoría
        categoryRepository.findById(event.getCategoryId()).ifPresent(category -> {
            dto.setCategoryId(category.getId());
            dto.setCategoryName(category.getName());
        });

        // Obtener precios
        List<AreaPricingEntity> prices = areaPricingRepository.findAllByEventId(event.getId());
        if (!prices.isEmpty()) {
            AreaPricingEntity minPrice = prices.stream()
                    .min(Comparator.comparing(AreaPricingEntity::getPriceCents))
                    .orElse(null);
            if (minPrice != null) {
                dto.setMinPriceCents(minPrice.getPriceCents());
                dto.setCurrency(minPrice.getCurrency());
            }
        }

        // Obtener disponibilidad
        Integer available = availabilityService.getAvailableTicketsForEvent(event.getId());
        dto.setTotalAvailableTickets(available);
        dto.setTotalCapacity(available); // Por ahora son iguales

        return dto;
    }

    private EventDetailDTO mapToEventDetail(EventEntity event) {
        EventDetailDTO dto = EventDetailDTO.builder()
                .id(event.getId())
                .organizerId(event.getOrganizerId())
                .title(event.getTitle())
                .slug(event.getSlug())
                .description(event.getDescription())
                .coverUrl(event.getCoverUrl())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .venueName(event.getVenueName())
                .venueDescription(event.getVenueDescription())
                .addressLine(event.getAddressLine())
                .city(event.getCity())
                .state(event.getState())
                .country(event.getCountry())
                .lat(event.getLat())
                .lng(event.getLng())
                .startsAt(event.getStartsAt())
                .endsAt(event.getEndsAt())
                .build();

        // Obtener categoría completa
        categoryRepository.findById(event.getCategoryId()).ifPresent(category -> {
            dto.setCategoryId(category.getId());
            dto.setCategoryName(category.getName());
            dto.setCategoryDescription(category.getDescription());
        });

        // Obtener áreas con precios y disponibilidad
        List<AreaEntity> areas = areaRepository.findByEventId(event.getId());
        List<EventDetailDTO.AreaDetailDTO> areaDTOs = new ArrayList<>();
        
        Long minPrice = null;
        Long maxPrice = null;
        Integer totalAvailable = 0;
        Integer totalCapacity = 0;
        String currency = "ARS";

        for (AreaEntity area : areas) {
            // Obtener precio del área
            Optional<AreaPricingEntity> pricing = areaPricingRepository.findByAreaId(area.getId());
            Long priceCents = null;
            if (pricing.isPresent()) {
                priceCents = pricing.get().getPriceCents();
                currency = pricing.get().getCurrency();
                
                if (minPrice == null || priceCents < minPrice) {
                    minPrice = priceCents;
                }
                if (maxPrice == null || priceCents > maxPrice) {
                    maxPrice = priceCents;
                }
            }

            // Obtener disponibilidad del área
            Integer areaAvailable = availabilityService.getAvailableTicketsForArea(area.getId());
            Integer areaCapacity = area.getCapacity() != null ? area.getCapacity() : 
                    seatRepository.findByAreaId(area.getId()).size();

            totalAvailable += areaAvailable;
            totalCapacity += areaCapacity;

            // Construir DTO del área
            EventDetailDTO.AreaDetailDTO areaDTO = EventDetailDTO.AreaDetailDTO.builder()
                    .id(area.getId())
                    .name(area.getName())
                    .isGeneralAdmission(area.getIsGeneralAdmission())
                    .capacity(area.getCapacity())
                    .position(area.getPosition())
                    .priceCents(priceCents)
                    .currency(currency)
                    .availableTickets(areaAvailable)
                    .totalSeats(areaCapacity)
                    .build();

            areaDTOs.add(areaDTO);
        }

        dto.setAreas(areaDTOs);
        dto.setTotalAvailableTickets(totalAvailable);
        dto.setTotalCapacity(totalCapacity);
        dto.setMinPriceCents(minPrice);
        dto.setMaxPriceCents(maxPrice);
        dto.setCurrency(currency);

        return dto;
    }
}

