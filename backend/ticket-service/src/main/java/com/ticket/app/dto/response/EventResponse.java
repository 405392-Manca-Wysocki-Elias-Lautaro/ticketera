package com.ticket.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private String eventTitle;
    private String eventDescription;
    private String venueName;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String categoryName;
    private String startsAt;
    private String endsAt;
    private AreaResponse area;
}
