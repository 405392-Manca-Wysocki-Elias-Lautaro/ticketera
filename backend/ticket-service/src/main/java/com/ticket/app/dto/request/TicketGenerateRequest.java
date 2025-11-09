package com.ticket.app.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketGenerateRequest {

    @NotNull(message = "The field 'orderItemId' is required.")
    private UUID orderItemId;

    @NotNull(message = "The field 'occurrenceId' is required.")
    private UUID occurrenceId;

    // 💰 Pricing fields
    @NotNull(message = "The field 'price' is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "The price must be greater than 0.")
    private BigDecimal price;

    @NotBlank(message = "The field 'currency' is required.")
    @Size(min = 3, max = 10, message = "Currency code must be between 3 and 10 characters.")
    private String currency;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount cannot be negative.")
    private BigDecimal discount;

    @NotNull(message = "The field 'finalPrice' is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "The final price must be greater than 0.")
    private BigDecimal finalPrice;

    // ⏰ Event timing
    @NotNull(message = "The field 'eventStart' is required.")
    private OffsetDateTime eventStart;

    private OffsetDateTime eventEnd;
}
