package com.ticket.app.dto.response;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * 🌐 Estructura estándar de respuesta API para el módulo Tickets
 */
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private OffsetDateTime timestamp;
}
