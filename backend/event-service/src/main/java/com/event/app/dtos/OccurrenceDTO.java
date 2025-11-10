package com.event.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OccurrenceDTO {

    private UUID id;

    @NotNull(message = "El ID del evento es obligatorio")
    private UUID eventId;

    @NotNull(message = "El ID del venue es obligatorio")
    private UUID venueId;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(draft|published|cancelled|completed)$", message = "El estado debe ser: draft, published, cancelled o completed")
    private String status;

    @Size(max = 200, message = "El slug no puede superar los 200 caracteres")
    @Pattern(regexp = "^[a-z0-9-]*$", message = "El slug solo puede contener minúsculas, números y guiones")
    private String slug;
}

