package com.ticket.app.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * ❌ Estructura estándar de error API para el módulo Tickets
 */
@Data
@Builder
public class ApiError {
    private int status;                  // HTTP status code (ej: 404)
    private String code;                 // Código interno de error (ej: TICKET_NOT_FOUND)
    private String message;              // Mensaje legible para el cliente
    private Map<String, String> details; // Campos o información adicional
}
