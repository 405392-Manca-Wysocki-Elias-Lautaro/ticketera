package com.ticket.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket.app.domain.entities.NotificationLog;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    Optional<NotificationLog> findByTicketIdAndReminderType(UUID ticketId, String reminderType);
    boolean existsByTicketIdAndReminderType(UUID ticketId, String reminderType);
}
