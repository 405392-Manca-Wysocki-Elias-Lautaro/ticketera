package com.ticket.app.services.impl;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticket.app.domain.entities.Hold;
import com.ticket.app.domain.entities.Ticket;
import com.ticket.app.domain.entities.TicketStatusHistory;
import com.ticket.app.domain.enums.HoldStatus;
import com.ticket.app.domain.enums.TicketStatus;
import com.ticket.app.domain.models.TicketModel;
import com.ticket.app.exception.exceptions.InvalidTicketStatusException;
import com.ticket.app.exception.exceptions.TicketAlreadyCheckedInException;
import com.ticket.app.exception.exceptions.TicketNotFoundException;
import com.ticket.app.repositories.HoldRepository;
import com.ticket.app.repositories.TicketRepository;
import com.ticket.app.repositories.TicketStatusHistoryRepository;
import com.ticket.app.services.TicketService;
import com.ticket.app.utils.QrGeneratorUtil;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository historyRepository;
    private final HoldRepository holdRepository;
    private final ModelMapper modelMapper;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            TicketStatusHistoryRepository historyRepository,
            HoldRepository holdRepository,
            ModelMapper modelMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.historyRepository = historyRepository;
        this.holdRepository = holdRepository;
        this.modelMapper = modelMapper;
    }

    // ------------------------------------------------------------
    // 🎟️ Generar nuevo ticket
    // ------------------------------------------------------------
    @Override
    @Transactional
    public TicketModel generateTicket(UUID orderItemId, UUID occurrenceId) {
        try {
            // Si existe un hold activo para este cliente/ocurrencia → convertirlo
            Optional<Hold> holdOpt = holdRepository.findActiveByOccurrenceId(occurrenceId);
            holdOpt.ifPresent(hold -> {
                hold.setStatus(HoldStatus.CONVERTED);
                hold.setUpdatedAt(OffsetDateTime.now());
                holdRepository.save(hold);
            });

            // Crear ticket base
            TicketModel model = new TicketModel();
            model.setOrderItemId(orderItemId);
            model.setOccurrenceId(occurrenceId);
            model.setCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            model.setQrToken(UUID.randomUUID().toString());
            model.setStatus(TicketStatus.ISSUED);
            model.setIssuedAt(OffsetDateTime.now());
            model.setCreatedAt(OffsetDateTime.now());
            model.setUpdatedAt(OffsetDateTime.now());

            String qrBase64 = QrGeneratorUtil.generateQRCodeBase64(model.getQrToken(), 250, 250);
            model.setQrBase64("data:image/png;base64," + qrBase64);

            // Guardar ticket
            Ticket saved = ticketRepository.save(modelMapper.map(model, Ticket.class));

            // Log de estado inicial
            logTicketStatusChange(saved, null, TicketStatus.ISSUED, "Ticket generado");

            TicketModel response = modelMapper.map(saved, TicketModel.class);
            response.setQrBase64(model.getQrBase64());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error generando el ticket: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------
    // ✅ Validar ticket (escaneo)
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
        TicketStatus fromStatus = entity.getStatus();

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
        logTicketStatusChange(saved, fromStatus, TicketStatus.CHECKED_IN, "Ticket validado en puerta");

        return modelMapper.map(saved, TicketModel.class);
    }

    // ------------------------------------------------------------
    // 🧾 Registrar cambios en el historial de estado
    // ------------------------------------------------------------
    private void logTicketStatusChange(Ticket ticket, TicketStatus from, TicketStatus to, String note) {
        TicketStatusHistory history = new TicketStatusHistory();
        history.setTicket(ticket);
        history.setFromStatus(from != null ? from.name().toLowerCase() : null);
        history.setToStatus(to.name().toLowerCase());
        history.setUpdatedUser(null); // se puede setear con el usuario del contexto si existe
        history.setUpdatedAt(OffsetDateTime.now());
        history.setNote(note);

        historyRepository.save(history);
    }
}
