package com.payment.app.pkg.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir notificaciones webhook de Mercado Pago
 * Basado en la documentación oficial:
 * https://www.mercadopago.com.ar/developers/es/docs/checkout-pro/payment-notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookNotificationRequest {
    
    /**
     * Tipo de notificación recibida
     * Ejemplos: "payment", "plan", "subscription", "invoice", "point_integration_wh"
     */
    private String action;
    
    /**
     * Fecha de la notificación en formato ISO 8601
     */
    @JsonProperty("date_created")
    private String dateCreated;
    
    /**
     * ID de la notificación
     */
    private Long id;
    
    /**
     * Indica si es una notificación de prueba
     */
    @JsonProperty("live_mode")
    private Boolean liveMode;
    
    /**
     * Tipo de notificación (ej: "payment")
     */
    private String type;
    
    /**
     * ID del usuario de Mercado Pago
     */
    @JsonProperty("user_id")
    private String userId;
    
    /**
     * Información del recurso notificado
     */
    private DataInfo data;
    
    /**
     * Clase interna para la información del recurso
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataInfo {
        /**
         * ID del recurso (ej: ID del pago)
         */
        private String id;
    }
}

