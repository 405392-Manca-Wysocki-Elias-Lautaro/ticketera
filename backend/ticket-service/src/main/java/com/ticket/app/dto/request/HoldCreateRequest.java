package com.ticket.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class HoldCreateRequest {

    @NotNull(message = "El campo customerId es obligatorio.")
    private UUID customerId;

    @NotNull(message = "El campo occurrenceId es obligatorio.")
    private UUID occurrenceId;

    private UUID eventVenueAreaId;
    private UUID eventVenueSeatId;
    private Integer quantity = 1;
}
