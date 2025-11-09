package com.ticket.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketValidateRequest {

    @NotBlank
    private String type; // "QR" o "CODE"

    @NotBlank
    private String value;
}
