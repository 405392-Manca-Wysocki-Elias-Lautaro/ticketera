package com.event.app.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueAreaDTO {

    private UUID areaId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID venueId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String name;

    @NotNull(message = "El campo isGeneralAdmission es obligatorio")
    private Boolean isGeneralAdmission;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacity;

    @Min(value = 0, message = "La posición no puede ser negativa")
    private Integer position;
}

