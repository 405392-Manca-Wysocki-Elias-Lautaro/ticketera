package com.event.app.models;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueSeat {

    private UUID id;

    private UUID venueAreaId;

    private Integer seatNumber;

    private Integer rowNumber;

    private String label; // Concatenación de rowNumber + seatNumber
}

