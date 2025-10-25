package com.event.app.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueSeatDTO {

    private UUID seatId;

    private UUID venueAreaId;

    private Integer rowNumber;

    private Integer seatNumber;

    private String label; // Concatenación de rowNumber + seatNumber para el frontend (ej: 1-1, 1-2, 1-3, ...)
}

