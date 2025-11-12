package com.event.app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private UUID id;

    private UUID organizerId;

    private String title;

    private String slug;

    private String description;

    private UUID categoryId;

    private String coverUrl;

    private String status;

    // Campos de venue
    private String venueName;

    private String venueDescription;

    private String addressLine;

    private String city;

    private String state;

    private String country;

    private BigDecimal lat;

    private BigDecimal lng;

    // Campos de occurrence
    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private LocalDateTime createdAt;

    private Boolean active;
}
