package com.event.app.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaPricing {

    private UUID id;

    private UUID areaId;

    private Long priceCents;

    private String currency;

    private LocalDateTime createdAt;
}

