package com.event.app.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueSeatDTO {

    private UUID id;

    private UUID venueAreaId;

    private String seatLabel;

    private String rowLabel;

    private String numberLabel;
}

