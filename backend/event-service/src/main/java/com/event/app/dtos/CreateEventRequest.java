package com.event.app.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

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

    @NotNull(message = "El ID de categoría es obligatorio")
    private UUID categoryId;

    @Size(max = 500, message = "La URL de portada no puede superar los 500 caracteres")
    private String coverUrl;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(draft|published|cancelled|completed)$", message = "El estado debe ser: draft, published, cancelled o completed")
    private String status;

    // Campos de venue
    @NotBlank(message = "El nombre del lugar es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre del lugar debe tener entre 3 y 200 caracteres")
    private String venueName;

    @Size(max = 2000, message = "La descripción del lugar no puede superar los 2000 caracteres")
    private String venueDescription;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 500, message = "La dirección no puede superar los 500 caracteres")
    private String addressLine;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String city;

    @NotBlank(message = "El estado/provincia es obligatorio")
    @Size(max = 100, message = "El estado/provincia no puede superar los 100 caracteres")
    private String state;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String country;

    private BigDecimal lat;

    private BigDecimal lng;

    // Campos de occurrence
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    // Áreas con asientos y precios
    @Valid
    private List<CreateAreaRequest> areas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAreaRequest {
        
        @NotBlank(message = "El nombre del área es obligatorio")
        @Size(min = 3, max = 200, message = "El nombre del área debe tener entre 3 y 200 caracteres")
        private String name;

        @NotNull(message = "Debe especificar si es admisión general")
        private Boolean isGeneralAdmission;

        private Integer capacity;

        private Integer position;

        @NotNull(message = "El precio es obligatorio")
        private Long priceCents;

        private String currency;

        @Valid
        private List<CreateSeatRequest> seats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSeatRequest {
        
        @NotNull(message = "El número de asiento es obligatorio")
        private Integer seatNumber;

        @NotNull(message = "El número de fila es obligatorio")
        private Integer rowNumber;

        @NotBlank(message = "La etiqueta del asiento es obligatoria")
        @Size(min = 1, max = 50, message = "La etiqueta del asiento debe tener entre 1 y 50 caracteres")
        private String label;
    }
}

