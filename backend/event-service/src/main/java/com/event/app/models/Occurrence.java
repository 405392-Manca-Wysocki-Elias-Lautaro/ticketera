package com.event.app.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Occurrence {

    private UUID id;

    private UUID eventId;

    private UUID venueId;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private String status;

    private String slug;

    private Boolean active;
}

