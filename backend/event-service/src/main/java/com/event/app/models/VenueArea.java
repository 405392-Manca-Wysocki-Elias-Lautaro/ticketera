package com.event.app.models;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueArea {

    private Long id;

    private UUID venueId;

    private String name;

    private Boolean isGeneralAdmission;

    private Integer capacity;

    private Integer position;
}

