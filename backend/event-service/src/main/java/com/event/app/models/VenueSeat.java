package com.event.app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueSeat {

    private Long id;

    private Long venueAreaId;

    private String seatLabel;

    private String rowLabel;

    private String numberLabel;
}

