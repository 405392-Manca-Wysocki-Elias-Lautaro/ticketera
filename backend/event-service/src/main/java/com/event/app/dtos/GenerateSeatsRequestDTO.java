package com.event.app.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSeatsRequestDTO {

    @NotNull(message = "El número de filas es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 fila")
    private Integer rows;

    @NotNull(message = "El número de asientos por fila es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 asiento por fila")
    private Integer seatsPerRow;

    private String rowPrefix; // Por ejemplo: "Fila ", "Row ", etc. / VER ESTO

    private Integer startingRowNumber; // Número inicial para las filas (por defecto 1) / VER ESTO

    private Integer startingSeatNumber; // Número inicial para los asientos (por defecto 1) / VER ESTO
}

