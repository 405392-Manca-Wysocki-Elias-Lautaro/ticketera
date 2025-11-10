package com.event.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "occurrences", schema="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String slug;

    @Column(nullable = false)
    private Boolean active;
}

