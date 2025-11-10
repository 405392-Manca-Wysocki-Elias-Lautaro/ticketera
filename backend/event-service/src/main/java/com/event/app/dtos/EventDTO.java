package com.event.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private UUID id;

    @NotNull(message = "El ID del organizador es obligatorio")
    private UUID organizerId;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String title;

    @NotBlank(message = "El slug es obligatorio")
    @Size(min = 3, max = 200, message = "El slug debe tener entre 3 y 200 caracteres")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "El slug solo puede contener minúsculas, números y guiones")
    private String slug;

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    private String description;

    private UUID categoryId;

    @Size(max = 500, message = "La URL de portada no puede superar los 500 caracteres")
    private String coverUrl;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(draft|published|cancelled|completed)$", message = "El estado debe ser: draft, published, cancelled o completed")
    private String status;
}

