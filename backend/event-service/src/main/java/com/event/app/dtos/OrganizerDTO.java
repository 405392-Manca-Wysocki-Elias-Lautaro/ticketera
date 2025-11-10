package com.event.app.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerDTO {

    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    @Size(min = 3, max = 30, message = "El slug debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "El slug solo puede contener minúsculas, números y guiones")
    private String slug;

    @NotBlank(message = "El email de contacto es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String contactEmail;

    @Pattern(regexp = "^[0-9]{8,15}$", message = "El teléfono debe contener solo números y tener entre 8 y 15 dígitos")
    private String phoneNumber;

}
