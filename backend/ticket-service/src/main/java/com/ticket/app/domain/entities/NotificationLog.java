package com.ticket.app.domain.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_logs", schema = "tickets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ticket_id", "reminder_type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Column(name = "reminder_type", nullable = false)
    private String reminderType; // e.g., "EVENT_START_24H", "EVENT_START_1H"

    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private OffsetDateTime sentAt = OffsetDateTime.now();
    
    @Column(name = "status")
    private String status; // SENT, FAILED, etc.
}
