package com.payment.app.pkg.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO para la respuesta del endpoint de MercadoPago GET /v1/payments/{id}
 * Contiene solo los campos relevantes para nuestro sistema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoPaymentResponse {
    
    /**
     * ID del pago en MercadoPago
     */
    private Long id;
    
    /**
     * Estado del pago
     * Valores posibles: "pending", "approved", "authorized", "in_process", "in_mediation", 
     * "rejected", "cancelled", "refunded", "charged_back"
     */
    private String status;
    
    /**
     * Detalle del estado del pago
     */
    @JsonProperty("status_detail")
    private String statusDetail;
    
    /**
     * Referencia externa (nuestro Order ID)
     */
    @JsonProperty("external_reference")
    private String externalReference;
    
    /**
     * ID de la preferencia usada para crear el pago
     */
    @JsonProperty("preference_id")
    private String preferenceId;
    
    /**
     * Monto de la transacción
     */
    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;
    
    /**
     * Monto neto recibido
     */
    @JsonProperty("transaction_amount_refunded")
    private BigDecimal transactionAmountRefunded;
    
    /**
     * Moneda
     */
    @JsonProperty("currency_id")
    private String currencyId;
    
    /**
     * Descripción del pago
     */
    private String description;
    
    /**
     * Email del pagador
     */
    @JsonProperty("payer")
    private PayerInfo payer;
    
    /**
     * Método de pago
     */
    @JsonProperty("payment_method_id")
    private String paymentMethodId;
    
    /**
     * Tipo de método de pago
     */
    @JsonProperty("payment_type_id")
    private String paymentTypeId;
    
    /**
     * Fecha de creación
     */
    @JsonProperty("date_created")
    private OffsetDateTime dateCreated;
    
    /**
     * Fecha de aprobación
     */
    @JsonProperty("date_approved")
    private OffsetDateTime dateApproved;
    
    /**
     * Fecha de última actualización
     */
    @JsonProperty("date_last_updated")
    private OffsetDateTime dateLastUpdated;
    
    /**
     * Información del pagador
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayerInfo {
        private Long id;
        private String email;
        
        @JsonProperty("first_name")
        private String firstName;
        
        @JsonProperty("last_name")
        private String lastName;
    }
}

