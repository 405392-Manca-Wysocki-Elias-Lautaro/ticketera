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
public class SeatDTO {

    private UUID id;

    @NotNull(message = "El ID del área es obligatorio")
    private UUID areaId;

    @NotNull(message = "El número de asiento es obligatorio")
    private Integer seatNumber;

    @NotNull(message = "El número de fila es obligatorio")
    private Integer rowNumber;

    @NotBlank(message = "La etiqueta del asiento es obligatoria")
    @Size(min = 1, max = 50, message = "La etiqueta del asiento debe tener entre 1 y 50 caracteres")
    private String label;
}

