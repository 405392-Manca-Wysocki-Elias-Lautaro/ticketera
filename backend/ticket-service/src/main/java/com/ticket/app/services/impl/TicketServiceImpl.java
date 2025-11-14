package com.ticket.app.services.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.ticket.app.clients.EventClient;
import com.ticket.app.domain.entities.Ticket;
import com.ticket.app.domain.entities.TicketStatusHistory;
import com.ticket.app.domain.enums.HoldStatus;
import com.ticket.app.domain.enums.TicketStatus;
import com.ticket.app.domain.enums.UserRole;
import com.ticket.app.domain.models.AreaModel;
import com.ticket.app.domain.models.EventModel;
import com.ticket.app.domain.models.TicketModel;
import com.ticket.app.dto.request.TicketGenerateRequest;
import com.ticket.app.exception.exceptions.InvalidTicketStatusException;
import com.ticket.app.exception.exceptions.TicketAlreadyCheckedInException;
import com.ticket.app.exception.exceptions.TicketExpiredException;
import com.ticket.app.exception.exceptions.TicketNotFoundException;
import com.ticket.app.exception.exceptions.UnauthorizedTicketAccessException;
import com.ticket.app.repositories.HoldRepository;
import com.ticket.app.repositories.TicketRepository;
import com.ticket.app.repositories.TicketStatusHistoryRepository;
import com.ticket.app.services.TicketService;
import com.ticket.app.utils.JwtUtils;
import com.ticket.app.utils.QrGeneratorUtil;
import com.ticket.app.utils.RoleUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository historyRepository;
    private final HoldRepository holdRepository;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;
    private final EventClient eventClient;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketStatusHistoryRepository historyRepository,
            HoldRepository holdRepository,
            ModelMapper modelMapper,
            JwtUtils jwtUtils,
            EventClient eventClient
    ) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.holdRepository = holdRepository;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
        this.eventClient = eventClient;
    }

    // ------------------------------------------------------------
    // 🎟️ Generate new ticket
    // ------------------------------------------------------------
    @Override
    @Transactional
    public TicketModel generateTicket(TicketGenerateRequest request) {
        try {
            // 1️⃣ Convert hold if exists
            holdRepository.findActiveByeventId(request.getEventId())
                    .ifPresent(hold -> {
                        hold.setStatus(HoldStatus.CONVERTED);
                        hold.setUpdatedAt(OffsetDateTime.now());
                        holdRepository.save(hold);
                    });

            // 2️⃣ Determine expiration time
            OffsetDateTime expiration = (request.getEventEnd() != null)
                    ? request.getEventEnd()
                    : request.getEventStart().plusHours(6);

            // 3️⃣ Create ticket
            TicketModel model = new TicketModel();
            model.setOrderItemId(request.getOrderItemId());
            model.setEventId(request.getEventId());
            model.setEventVenueAreaId(request.getEventVenueAreaId());
            model.setEventVenueSeatId(request.getEventVenueSeatId());
            model.setUserId(jwtUtils.getUserId());
            model.setCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            model.setQrToken(UUID.randomUUID().toString());
            model.setStatus(TicketStatus.ISSUED);
            model.setIssuedAt(OffsetDateTime.now());
            model.setCreatedAt(OffsetDateTime.now());
            model.setUpdatedAt(OffsetDateTime.now());
            model.setExpiresAt(expiration);

            // 💰 Pricing snapshot
            model.setPrice(request.getPrice());
            model.setCurrency(request.getCurrency());
            model.setDiscount(request.getDiscount());
            model.setFinalPrice(request.getFinalPrice());

            // 4️⃣ Generate QR
            String qrBase64 = QrGeneratorUtil.generateQRCodeBase64(model.getQrToken(), 250, 250);
            model.setQrBase64("data:image/png;base64," + qrBase64);

            // 5️⃣ Persist ticket
            Ticket saved = ticketRepository.save(modelMapper.map(model, Ticket.class));

            logTicketStatusChange(saved, null, TicketStatus.ISSUED, "Ticket issued");

            TicketModel response = modelMapper.map(saved, TicketModel.class);
            response.setQrBase64(model.getQrBase64());
            return this.getById(response.getId());

        } catch (Exception e) {
            throw new RuntimeException("Error generating ticket: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------
    // ✅ Validate ticket (scan)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public TicketModel validateByQrToken(String qrToken) {

        UserRole role = jwtUtils.getRole();

        if (!RoleUtil.isPrivileged(role)) {
            throw new UnauthorizedTicketAccessException();
        }

        Ticket entity = ticketRepository.findByQrToken(qrToken)
                .orElseThrow(TicketNotFoundException::new);
        return validateAndUpdate(entity);
    }

    @Override
    @Transactional
    public TicketModel validateByCode(String code) {
        Ticket entity = ticketRepository.findByCode(code)
                .orElseThrow(TicketNotFoundException::new);

        return validateAndUpdate(entity);
    }

    private TicketModel validateAndUpdate(Ticket entity) {

        UUID currentUserId = jwtUtils.getUserId();
        UserRole role = jwtUtils.getRole();

        if (!entity.getUserId().equals(currentUserId) && !RoleUtil.isPrivileged(role)) {
            throw new UnauthorizedTicketAccessException();
        }

        TicketStatus fromStatus = entity.getStatus();

        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new TicketExpiredException();
        }
        if (fromStatus == TicketStatus.CHECKED_IN) {
            throw new TicketAlreadyCheckedInException();
        }
        if (fromStatus != TicketStatus.ISSUED) {
            throw new InvalidTicketStatusException();
        }

        entity.setStatus(TicketStatus.CHECKED_IN);
        entity.setCheckedInAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        Ticket saved = ticketRepository.save(entity);
        logTicketStatusChange(saved, fromStatus, TicketStatus.CHECKED_IN, "Ticket validated at entry");

        return modelMapper.map(saved, TicketModel.class);
    }

    // ------------------------------------------------------------
    // 🧾 Register status change history
    // ------------------------------------------------------------
    private void logTicketStatusChange(Ticket ticket, TicketStatus from, TicketStatus to, String note) {
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicket(ticket);
        history.setFromStatus(from != null ? from.name().toLowerCase() : null);
        history.setToStatus(to.name().toLowerCase());
        history.setUpdatedUser(jwtUtils.getUserId());
        history.setUpdatedAt(OffsetDateTime.now());
        history.setNote(note);

        historyRepository.save(history);
    }

    // ------------------------------------------------------------
    // 🔍 Get ticket by ID
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public TicketModel getById(UUID id) {

        UserRole role = jwtUtils.getRole();
        if (!RoleUtil.hasAnyRole(role, UserRole.SUPER_ADMIN)) {
            throw new UnauthorizedTicketAccessException();
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        TicketModel ticketModel = modelMapper.map(ticket, TicketModel.class);

        JsonNode eventJson = eventClient.getEventById(ticket.getEventId());
        JsonNode eventData = eventJson.get("data");

        if (eventData == null) {
            throw new RuntimeException("Event service returned null data");
        }

        List<AreaModel> areas = new ArrayList<>();

        if (eventData.has("areas") && eventData.get("areas").isArray()) {
            for (JsonNode areaNodeJson : eventData.get("areas")) {
                AreaModel area = AreaModel.builder()
                        .id(areaNodeJson.get("id").asText())
                        .name(areaNodeJson.get("name").asText())
                        .isGeneralAdmission(areaNodeJson.get("isGeneralAdmission").asBoolean())
                        .capacity(areaNodeJson.get("capacity").asInt())
                        .position(areaNodeJson.get("position").asInt())
                        .priceCents(new BigDecimal(areaNodeJson.get("priceCents").asText()))
                        .currency(areaNodeJson.get("currency").asText())
                        .availableTickets(areaNodeJson.get("availableTickets").asInt())
                        .totalSeats(areaNodeJson.get("totalSeats").asInt())
                        .build();

                areas.add(area);
            }
        }
        log.info("areas: {}", areas);
        

        log.info("eventVenueAreaId: {}", ticket.getEventVenueAreaId());


        AreaModel selectedArea = null;

        if (ticket.getEventVenueAreaId() != null) {
            String areaId = ticket.getEventVenueAreaId().toString();

            selectedArea = areas.stream()
                    .filter(a -> a.getId().equals(areaId))
                    .findFirst()
                    .orElse(null);
        }

        log.info("area: {}", selectedArea);

        EventModel eventModel = EventModel.builder()
                .eventTitle(eventData.get("title").asText())
                .eventDescription(eventData.get("description").asText())
                .venueName(eventData.get("venueName").asText())
                .addressLine(eventData.get("addressLine").asText())
                .city(eventData.get("city").asText())
                .state(eventData.get("state").asText())
                .country(eventData.get("country").asText())
                .categoryName(eventData.get("categoryName").asText())
                .startsAt(eventData.get("startsAt").asText())
                .endsAt(eventData.get("endsAt").asText())
                .area(selectedArea)
                .build();

        return TicketModel.builder()
                .id(ticketModel.getId())
                .orderItemId(ticketModel.getOrderItemId())
                .eventId(ticketModel.getEventId())
                .eventVenueAreaId(ticketModel.getEventVenueAreaId())
                .eventVenueSeatId(ticketModel.getEventVenueSeatId())
                .userId(ticketModel.getUserId())
                .code(ticketModel.getCode())
                .qrBase64(ticketModel.getQrBase64())
                .status(ticketModel.getStatus())
                .issuedAt(ticketModel.getIssuedAt())
                .checkedInAt(ticketModel.getCheckedInAt())
                .canceledAt(ticketModel.getCanceledAt())
                .refundedAt(ticketModel.getRefundedAt())
                .event(eventModel)
                .build();
    }

    // ------------------------------------------------------------
    // 👤 Get all tickets by User ID
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TicketModel> getByUserId() {

        UUID userId = jwtUtils.getUserId();

        List<Ticket> tickets = ticketRepository.findByUserId(userId);

        return tickets.stream()
                .map(ticket -> modelMapper.map(ticket, TicketModel.class))
                .toList();
    }

}
