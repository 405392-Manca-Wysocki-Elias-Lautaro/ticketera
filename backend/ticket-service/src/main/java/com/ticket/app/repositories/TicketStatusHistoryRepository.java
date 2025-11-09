package com.ticket.app.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ticket.app.domain.entities.TicketStatusHistory;

public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, UUID> {
}
