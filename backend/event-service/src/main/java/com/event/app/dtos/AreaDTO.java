package com.event.app.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDTO {

    private UUID id;

    @NotNull(message = "El ID del evento es obligatorio")
    private UUID eventId;

    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre del área debe tener entre 3 y 200 caracteres")
    private String name;

    @NotNull(message = "Debe especificar si es admisión general")
    private Boolean isGeneralAdmission;

    private Integer capacity;

    private Integer position;
}

