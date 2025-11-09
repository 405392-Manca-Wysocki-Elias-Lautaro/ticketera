package com.ticket.app.dto.request;


import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketGenerateRequest {

    @NotNull(message = "El campo orderItemId es obligatorio.")
    private UUID orderItemId;

    @NotNull(message = "El campo occurrenceId es obligatorio.")
    private UUID occurrenceId;
}
