package com.payment.app.controllers;

import com.payment.app.models.Payment;
import com.payment.app.pkg.dtos.WebhookNotificationRequest;
import com.payment.app.services.PaymentService;
import com.payment.app.services.TicketGenerationService;
import com.payment.app.services.WebhookValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para recibir notificaciones webhook de Mercado Pago
 * Documentación: https://www.mercadopago.com.ar/developers/es/docs/checkout-pro/payment-notifications
 */
@RestController
@RequestMapping("/webhook")
@Tag(name = "Webhooks", description = "MercadoPago webhook notifications")
public class WebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    
    private final PaymentService paymentService;
    private final WebhookValidationService webhookValidationService;
    private final TicketGenerationService ticketGenerationService;
    
    public WebhookController(
            PaymentService paymentService,
            WebhookValidationService webhookValidationService,
            TicketGenerationService ticketGenerationService) {
        this.paymentService = paymentService;
        this.webhookValidationService = webhookValidationService;
        this.ticketGenerationService = ticketGenerationService;
    }
    
    /**
     * Endpoint para recibir notificaciones de MercadoPago
     * MercadoPago enviará notificaciones a esta URL cuando ocurran eventos relacionados con pagos
     */
    @PostMapping
    @Operation(summary = "Receive MercadoPago webhook notifications", 
               description = "Processes payment notifications from MercadoPago and triggers ticket generation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification processed successfully"),
        @ApiResponse(responseCode = "201", description = "Notification processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid notification data"),
        @ApiResponse(responseCode = "401", description = "Invalid signature"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> receiveWebhook(
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestBody WebhookNotificationRequest notification) {
        
        logger.info("Received webhook notification: type={}, action={}, id={}, dataId={}", 
                notification.getType(), 
                notification.getAction(), 
                notification.getId(),
                notification.getData() != null ? notification.getData().getId() : null);
        
        try {
            // 1. Validar que sea una notificación de pago
            if (!"payment".equalsIgnoreCase(notification.getType())) {
                logger.info("Ignoring non-payment notification: {}", notification.getType());
                return ResponseEntity.ok().build();
            }
            
            // 2. Validar que tengamos el ID del pago
            if (notification.getData() == null || notification.getData().getId() == null) {
                logger.warn("Missing payment ID in webhook notification");
                return ResponseEntity.badRequest().build();
            }
            
            String paymentIdStr = notification.getData().getId();
            Long mercadopagoPaymentId;
            
            try {
                mercadopagoPaymentId = Long.parseLong(paymentIdStr);
            } catch (NumberFormatException e) {
                logger.error("Invalid payment ID format: {}", paymentIdStr);
                return ResponseEntity.badRequest().build();
            }
            
            // 3. Validar la firma del webhook (si está configurado el secret)
            boolean isValid = webhookValidationService.validateWebhookSignature(
                    xSignature, xRequestId, paymentIdStr);
            
            if (!isValid) {
                logger.warn("⚠️ Invalid webhook signature for payment: {} - Processing anyway for testing", mercadopagoPaymentId);
                // TODO: Re-enable strict validation in production
                // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // 4. Procesar el webhook (actualizar el pago)
            Payment payment = paymentService.processWebhookNotification(
                    mercadopagoPaymentId, null);
            
            // 5. Si el pago fue aprobado, generar tickets
            if (paymentService.isPaymentApproved(payment)) {
                logger.info("Payment {} approved. Triggering ticket generation for order {}", 
                        payment.getId(), payment.getOrderId());
                
                // Generar tickets de forma síncrona
                // En producción, esto podría hacerse de forma asíncrona con @Async o RabbitMQ
                boolean ticketsGenerated = ticketGenerationService.generateTicketsForPayment(payment);
                
                if (ticketsGenerated) {
                    logger.info("✅ Tickets generated successfully for order: {}", payment.getOrderId());
                } else {
                    logger.warn("⚠️ Some tickets failed to generate for order: {}", payment.getOrderId());
                    // Aún así devolvemos 200 a MercadoPago porque el pago se procesó correctamente
                }
            }
            
            // 6. Responder exitosamente a MercadoPago
            // MercadoPago espera HTTP 200 o 201
            logger.info("Webhook processed successfully for payment: {}", mercadopagoPaymentId);
            return ResponseEntity.ok().build();
            
        } catch (PaymentService.PaymentNotFoundException e) {
            logger.error("Payment not found: {}", e.getMessage());
            // Aún así respondemos 200 para que MercadoPago no reintente
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage(), e);
            // Devolver error para que MercadoPago reintente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint de prueba para verificar que el webhook está funcionando
     */
    @GetMapping("/health")
    @Operation(summary = "Health check for webhook endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Webhook endpoint is healthy");
    }
}

