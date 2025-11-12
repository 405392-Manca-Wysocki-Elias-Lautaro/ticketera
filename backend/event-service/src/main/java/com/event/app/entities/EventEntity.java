package com.event.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events", schema="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "cover_url", columnDefinition = "TEXT")
    private String coverUrl;

    @Column(nullable = false)
    private String status;

    // Campos de venue
    @Column(name = "venue_name", nullable = false, columnDefinition = "TEXT")
    private String venueName;

    @Column(name = "venue_description", columnDefinition = "TEXT")
    private String venueDescription;

    @Column(name = "address_line", nullable = false, columnDefinition = "TEXT")
    private String addressLine;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String city;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String state;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String country;

    @Column(precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(precision = 9, scale = 6)
    private BigDecimal lng;

    // Campos de occurrence
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active;
}

