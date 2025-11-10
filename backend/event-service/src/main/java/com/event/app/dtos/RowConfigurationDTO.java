package com.event.app.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RowConfigurationDTO {

    @NotNull(message = "El número de asientos por fila es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 asiento por fila")
    private Integer seatsPerRow; // Cantidad de asientos en esta fila específica
}
