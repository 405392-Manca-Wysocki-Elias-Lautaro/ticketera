package com.auth.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCatalog {

    // 🔐 Auth / Security
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_CREDENTIALS,
            "Email o contraseña incorrectos."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED,
            "No estás autorizado para acceder a este recurso."),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN,
            "No tienes permisos suficientes."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.TOKEN_EXPIRED,
            "El token de acceso ha expirado."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_TOKEN,
            "El token de acceso es inválido."),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, ErrorCodes.TOO_MANY_ATTEMPTS,
            "Demasiados intentos fallidos. Intenta nuevamente más tarde."),
    ACCOUNT_NOT_VERIFIED(HttpStatus.FORBIDDEN, ErrorCodes.ACCOUNT_NOT_VERIFIED,
            "Please verify your email before logging in."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_REFRESH_TOKEN,
            "Invalid or expired refresh token."),
    // 📥 Validaciones
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_FAILED,
            "Uno o más campos no cumplen con los requisitos."),
    MISSING_FIELDS(HttpStatus.BAD_REQUEST, ErrorCodes.MISSING_FIELDS,
            "Faltan campos obligatorios."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_FORMAT,
            "El formato de uno o más campos no es válido."),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCodes.WEAK_PASSWORD,
            "La contraseña no cumple con los requisitos de seguridad."),
    COMMON_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCodes.COMMON_PASSWORD,
            "La contraseña elegida es demasiado común o insegura."),
    // 🧑‍💼 Usuario / Roles
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, ErrorCodes.USER_ALREADY_EXISTS,
            "El usuario ya existe."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, ErrorCodes.EMAIL_ALREADY_EXISTS,
            "El email ya está registrado."),
    // ✅ Verificación de Email
    USER_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, ErrorCodes.USER_ALREADY_VERIFIED,
            "El usuario ya ha verificado su cuenta."),
    INVALID_OR_UNKNOWN_TOKEN(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_OR_UNKNOWN_TOKEN,
            "El token es inválido o no corresponde a ningún usuario."),
    TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST, ErrorCodes.TOKEN_ALREADY_USED,
            "El token de verificación ya fue utilizado."),
    EMAIL_VERIFICATION_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.EMAIL_VERIFICATION_TOKEN_EXPIRED,
            "El token de verificación ha expirado."),
    // ⚙️ Infra / Integraciones
    DATABASE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.DATABASE_UNAVAILABLE,
            "La base de datos no está disponible en este momento."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.SERVICE_UNAVAILABLE,
            "El servicio no está disponible. Intenta nuevamente más tarde."),
    INTEGRATION_ERROR(HttpStatus.BAD_GATEWAY, ErrorCodes.INTEGRATION_ERROR,
            "Error al comunicarse con un servicio externo."),
    STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_ERROR,
            "Error al acceder al sistema de almacenamiento."),
    // 🌐 Genéricos
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCodes.ENTITY_NOT_FOUND,
            "Recurso no encontrado."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCodes.BAD_REQUEST,
            "Solicitud inválida."),
    CONFLICT(HttpStatus.CONFLICT, ErrorCodes.CONFLICT,
            "Conflicto con los datos enviados."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR,
            "Error interno del servidor.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCatalog(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
