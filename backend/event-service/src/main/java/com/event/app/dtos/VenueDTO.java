package com.event.app.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueDTO {

    private Long id;

    @NotNull(message = "El ID del organizador es obligatorio")
    private Long organizerId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String name;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String description;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String addressLine;

    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String city;

    @Size(max = 100, message = "El estado/provincia no puede superar los 100 caracteres")
    private String state;

    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String country;

    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    private BigDecimal lat;

    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    private BigDecimal lng;
}

