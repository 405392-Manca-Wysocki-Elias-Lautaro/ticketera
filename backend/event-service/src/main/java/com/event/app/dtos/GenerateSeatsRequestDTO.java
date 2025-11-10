package com.event.app.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateSeatsRequestDTO {

    @NotEmpty(message = "Debe especificar al menos una configuración de fila")
    @Valid
    private List<RowConfigurationDTO> rows; // Arreglo de configuraciones de filas

    // Constructor de conveniencia para mantener compatibilidad con el formato anterior
    // (puede ser útil para migraciones o casos simples)
    public static GenerateSeatsRequestDTO createSimpleLayout(
            Integer numberOfRows, 
            Integer seatsPerRow) {
        
        List<RowConfigurationDTO> rowConfigs = new java.util.ArrayList<>();
        
        for (int i = 0; i < numberOfRows; i++) {
            rowConfigs.add(new RowConfigurationDTO(seatsPerRow));
        }
        
        return new GenerateSeatsRequestDTO(rowConfigs);
    }
}

