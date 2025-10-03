# 📘 Guía rápida: Validaciones en Spring Boot

Este documento resume las **anotaciones de validación de Jakarta Bean Validation** para usar junto a `ResponseEntity`.

---

## 🔹 Validaciones con Jakarta Bean Validation

Para validar datos de entrada en tus DTOs (`Request`), usá las anotaciones de `jakarta.validation.constraints`.

### 📌 Anotaciones comunes

- `@NotNull` → el valor no puede ser `null`.  
- `@NotBlank` → no puede ser `null`, vacío ni solo espacios.  
- `@NotEmpty` → no puede ser `null` ni vacío (`""`, `[]`).  
- `@Size(min, max)` → tamaño mínimo/máximo de string, lista o array.  
- `@Min(value)` / `@Max(value)` → valor numérico mínimo o máximo.  
- `@Positive`, `@PositiveOrZero` → debe ser positivo.  
- `@Negative`, `@NegativeOrZero` → debe ser negativo.  
- `@DecimalMin(value)`, `@DecimalMax(value)` → límites para decimales.  
- `@Digits(integer, fraction)` → controla dígitos enteros y decimales.  
- `@Email` → valida formato de correo electrónico.  
- `@Pattern(regexp)` → valida con expresión regular.  
- `@Past` → fecha anterior a hoy.  
- `@PastOrPresent` → fecha pasada o actual.  
- `@Future` → fecha posterior a hoy.  
- `@FutureOrPresent` → fecha futura o actual.  
- `@AssertTrue` / `@AssertFalse` → el valor debe ser `true` o `false`.

### 📌 Ejemplo de uso en un Request DTO

```java
public class RegisterRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank
    @Size(min = 12, message = "La contraseña debe tener al menos 12 caracteres")
    private String password;

    private String firstName;
    private String lastName;
}
