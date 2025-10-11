package com.event.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueSeatDTO {

    private Long id;

    private Long venueAreaId;

    private String seatLabel;

    private String rowLabel;

    private String numberLabel;
}

