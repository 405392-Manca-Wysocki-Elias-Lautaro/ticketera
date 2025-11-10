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
public class Venue {

    private UUID id;

    private UUID organizerId;

    private String name;

    private String description;

    private String addressLine;

    private String city;

    private String state;

    private String country;

    private BigDecimal lat;

    private BigDecimal lng;

    private LocalDateTime createdAt;

    private Boolean active;
}

