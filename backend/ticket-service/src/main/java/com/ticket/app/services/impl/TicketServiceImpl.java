package com.ticket.app.services.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticket.app.domain.entities.Ticket;
import com.ticket.app.domain.entities.TicketStatusHistory;
import com.ticket.app.domain.enums.HoldStatus;
import com.ticket.app.domain.enums.TicketStatus;
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

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository historyRepository;
    private final HoldRepository holdRepository;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketStatusHistoryRepository historyRepository,
            HoldRepository holdRepository,
            ModelMapper modelMapper,
            JwtUtils jwtUtils
    ) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.holdRepository = holdRepository;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
    }

    // ------------------------------------------------------------
    // 🎟️ Generate new ticket
    // ------------------------------------------------------------
    @Override
    @Transactional
    public TicketModel generateTicket(TicketGenerateRequest request) {
        try {
            // 1️⃣ Convert hold if exists
            UUID occurrenceUuid = UUID.nameUUIDFromBytes(("occurrence:" + request.getOccurrenceId()).getBytes());
            holdRepository.findActiveByOccurrenceId(occurrenceUuid)
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
            // Convert string IDs to UUIDs - for now we generate deterministic UUIDs from the strings
            model.setOrderItemId(UUID.nameUUIDFromBytes(("orderItem:" + request.getOrderItemId()).getBytes()));
            model.setOccurrenceId(UUID.nameUUIDFromBytes(("occurrence:" + request.getOccurrenceId()).getBytes()));
            // Use userId from request (for internal service calls) or JWT (for authenticated calls)
            UUID userId = request.getUserId() != null ? 
                UUID.fromString(request.getUserId()) : 
                jwtUtils.getUserId();
            model.setUserId(userId);
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

            logTicketStatusChange(saved, null, TicketStatus.ISSUED, "Ticket issued", userId);

            TicketModel response = modelMapper.map(saved, TicketModel.class);
            response.setQrBase64(model.getQrBase64());
            return response;

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
        String role = jwtUtils.getRole();

        List<String> allowedRoles = List.of("STAFF", "ADMIN", "SUPER_ADMIN");

        if (!entity.getUserId().equals(currentUserId) && !allowedRoles.contains(role)) {
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
        logTicketStatusChange(ticket, from, to, note, null);
    }
    
    private void logTicketStatusChange(Ticket ticket, TicketStatus from, TicketStatus to, String note, UUID userId) {
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicket(ticket);
        history.setFromStatus(from != null ? from.name().toLowerCase() : null);
        history.setToStatus(to.name().toLowerCase());
        
        // Use provided userId or get from JWT
        try {
            history.setUpdatedUser(userId != null ? userId : jwtUtils.getUserId());
        } catch (Exception e) {
            // If no JWT and no userId provided, use the ticket's userId
            history.setUpdatedUser(ticket.getUserId());
        }
        
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
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        TicketModel model = modelMapper.map(ticket, TicketModel.class);
        return model;
    }

    // ------------------------------------------------------------
    // 👤 Get all tickets by User ID
    // ------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<TicketModel> getByUserId(UUID userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);

        return tickets.stream()
                .map(ticket -> modelMapper.map(ticket, TicketModel.class))
                .toList();
    }

}
