package com.payment.app.services;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.payment.app.models.Payment;
import com.payment.app.pkg.dtos.CreatePaymentIntentRequest;
import com.payment.app.pkg.dtos.PaymentIntentResponse;
import com.payment.app.repositories.PaymentRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final PreferenceClient preferenceClient;
    
    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;
    
    @Value("${mercadopago.success-url:http://localhost:3000/my-tickets}")
    private String successUrl;
    
    @Value("${mercadopago.failure-url:http://localhost:3000/dashboard}")
    private String failureUrl;
    
    @Value("${mercadopago.pending-url:http://localhost:3000/my-tickets}")
    private String pendingUrl;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.preferenceClient = new PreferenceClient();
    }
    
    @PostConstruct
    public void init() {
        logger.info("PaymentService initialized with URLs - Success: '{}', Failure: '{}', Pending: '{}'", 
                    successUrl, failureUrl, pendingUrl);
    }
    
    /**
     * Crea una intención de pago con Mercado Pago Checkout Pro
     */
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        logger.info("Creating payment intent for order: {}", request.getOrderId());
        
        try {
            // 1. Crear el registro del pago en la base de datos
            Payment payment = createPaymentEntity(request);
            payment = paymentRepository.save(payment);
            
            // 2. Crear la preferencia en Mercado Pago
            Preference preference = createMercadoPagoPreference(request, payment);
            
            // 3. Actualizar el pago con la información de la preferencia
            payment.setPreferenceId(preference.getId());
            payment.setPaymentUrl(preference.getInitPoint());
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment = paymentRepository.save(payment);
            
            logger.info("Payment intent created successfully: {} with preference ID: {}", 
                    payment.getId(), preference.getId());
            
            return mapToResponse(payment);
            
        } catch (MPApiException e) {
            logger.error("MPApiException creating Mercado Pago preference for order {}", request.getOrderId());
            logger.error("Status Code: {}", e.getStatusCode());
            
            // Intentar obtener el contenido de la respuesta
            String responseContent = "No response content available";
            try {
                if (e.getApiResponse() != null) {
                    responseContent = e.getApiResponse().getContent();
                }
            } catch (Exception ex) {
                logger.error("Could not get API response content", ex);
            }
            
            logger.error("API Response Content: {}", responseContent);
            logger.error("Error message: {}", e.getMessage(), e);
            
            // Guardar el pago con error
            Payment failedPayment = createPaymentEntity(request);
            failedPayment.setStatus(Payment.PaymentStatus.FAILED);
            failedPayment.setErrorMessage("MercadoPago API Error [" + e.getStatusCode() + "]: " + responseContent);
            failedPayment = paymentRepository.save(failedPayment);
            
            throw new PaymentIntentException("Failed to create payment intent: " + responseContent, e);
        } catch (MPException e) {
            logger.error("MPException creating Mercado Pago preference for order {}: {}", 
                    request.getOrderId(), e.getMessage(), e);
            
            // Guardar el pago con error
            Payment failedPayment = createPaymentEntity(request);
            failedPayment.setStatus(Payment.PaymentStatus.FAILED);
            failedPayment.setErrorMessage("Error creating preference: " + e.getMessage());
            failedPayment = paymentRepository.save(failedPayment);
            
            throw new PaymentIntentException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crea la entidad Payment desde el request
     */
    private Payment createPaymentEntity(CreatePaymentIntentRequest request) {
        Payment.PaymentBuilder builder = Payment.builder()
                .orderId(request.getOrderId())
                .providerId(request.getProviderId())
                .providerRef(request.getProviderRef())
                .amountCents(request.getAmountCents())
                .currency(request.getCurrency())
                .status(Payment.PaymentStatus.PENDING);
        
        // Agregar metadata si está disponible
        if (request.getMetadata() != null) {
            builder.customerEmail(request.getMetadata().getCustomerEmail())
                   .customerName(request.getMetadata().getCustomerName())
                   .description(request.getMetadata().getDescription());
        }
        
        return builder.build();
    }
    
    /**
     * Crea la preferencia en Mercado Pago siguiendo la documentación oficial
     */
    private Preference createMercadoPagoPreference(CreatePaymentIntentRequest request, Payment payment) 
            throws MPException, MPApiException {
        
        // 1. Crear el item de la preferencia
        BigDecimal unitPrice = BigDecimal.valueOf(request.getAmountCents()).divide(BigDecimal.valueOf(100));
        
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id(String.valueOf(request.getOrderId()))
                .title(request.getMetadata() != null && request.getMetadata().getDescription() != null 
                        ? request.getMetadata().getDescription() 
                        : "Order #" + request.getOrderId())
                .description(request.getMetadata() != null && request.getMetadata().getDescription() != null 
                        ? request.getMetadata().getDescription() 
                        : "Payment for order #" + request.getOrderId())
                .quantity(1)
                .currencyId(request.getCurrency())
                .unitPrice(unitPrice)
                .build();
        
        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);
        
        // 2. Configurar las URLs de retorno
        PreferenceRequest.PreferenceRequestBuilder preferenceBuilder = PreferenceRequest.builder()
                .items(items)
                .externalReference(String.valueOf(request.getOrderId()));
        
        // Agregar URLs de retorno (prioridad: metadata > configuración)
        String finalSuccessUrl = successUrl;
        String finalFailureUrl = failureUrl;
        String finalPendingUrl = pendingUrl;
        
        logger.info("Initial URLs - Success: '{}', Failure: '{}', Pending: '{}'", 
                    finalSuccessUrl, finalFailureUrl, finalPendingUrl);
        
        if (request.getMetadata() != null) {
            if (request.getMetadata().getReturnUrl() != null) {
                finalSuccessUrl = request.getMetadata().getReturnUrl();
                finalPendingUrl = request.getMetadata().getReturnUrl();
            }
            if (request.getMetadata().getCancelUrl() != null) {
                finalFailureUrl = request.getMetadata().getCancelUrl();
            }
        }
        
        logger.info("Final URLs - Success: '{}', Failure: '{}', Pending: '{}'", 
                    finalSuccessUrl, finalFailureUrl, finalPendingUrl);
        
        // Configurar URLs de retorno
        // MercadoPago requiere que back_urls esté definido cuando se usa autoReturn
        if ((finalSuccessUrl != null && !finalSuccessUrl.isEmpty()) || 
            (finalFailureUrl != null && !finalFailureUrl.isEmpty()) || 
            (finalPendingUrl != null && !finalPendingUrl.isEmpty())) {
            
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(finalSuccessUrl != null && !finalSuccessUrl.isEmpty() ? finalSuccessUrl : null)
                    .failure(finalFailureUrl != null && !finalFailureUrl.isEmpty() ? finalFailureUrl : null)
                    .pending(finalPendingUrl != null && !finalPendingUrl.isEmpty() ? finalPendingUrl : null)
                    .build();
            
            preferenceBuilder.backUrls(backUrls);
            
            // Solo agregar autoReturn si success URL está definida Y NO es localhost
            // MercadoPago no acepta autoReturn con URLs de localhost
            if (finalSuccessUrl != null && !finalSuccessUrl.isEmpty() && 
                !finalSuccessUrl.contains("localhost") && !finalSuccessUrl.contains("127.0.0.1")) {
                preferenceBuilder.autoReturn("approved");
                logger.info("Auto-return enabled for success URL: {}", finalSuccessUrl);
            } else {
                logger.info("Auto-return NOT enabled (localhost URL or not defined)");
            }
        }
        
        // Agregar URL de notificación si está configurada
        if (notificationUrl != null && !notificationUrl.isEmpty()) {
            preferenceBuilder.notificationUrl(notificationUrl);
        }
        
        PreferenceRequest preferenceRequest = preferenceBuilder.build();
        
        // 3. Crear la preferencia
        logger.info("Creating Mercado Pago preference for order: {}", request.getOrderId());
        return preferenceClient.create(preferenceRequest);
    }
    
    /**
     * Mapea la entidad Payment a PaymentIntentResponse
     */
    private PaymentIntentResponse mapToResponse(Payment payment) {
        return PaymentIntentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .providerId(payment.getProviderId())
                .providerRef(payment.getProviderRef())
                .status(payment.getStatus().name())
                .amountCents(payment.getAmountCents())
                .currency(payment.getCurrency())
                .paymentUrl(payment.getPaymentUrl())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
    
    /**
     * Procesa una notificación de webhook de MercadoPago
     * 
     * @param mercadopagoPaymentId ID del pago en MercadoPago
     * @param externalReference Referencia externa (Order ID)
     * @return Payment actualizado
     */
    public Payment processWebhookNotification(Long mercadopagoPaymentId, String externalReference) {
        logger.info("Processing webhook notification for MercadoPago payment: {}, externalReference: {}", 
                mercadopagoPaymentId, externalReference);
        
        try {
            // 1. Primero consultar el pago en MercadoPago para obtener el external_reference
            com.mercadopago.client.payment.PaymentClient paymentClient = 
                    new com.mercadopago.client.payment.PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = 
                    paymentClient.get(mercadopagoPaymentId);
            
            // Obtener el external_reference de MercadoPago si no lo tenemos
            if (externalReference == null && mpPayment.getExternalReference() != null) {
                externalReference = mpPayment.getExternalReference();
                logger.info("External reference obtained from MercadoPago: {}", externalReference);
            }
            
            // 2. Buscar el pago en nuestra base de datos
            Payment payment = findPaymentByReference(mercadopagoPaymentId, externalReference);
            
            if (payment == null) {
                logger.error("Payment not found for MercadoPago ID: {} or external reference: {}", 
                        mercadopagoPaymentId, externalReference);
                throw new PaymentNotFoundException("Payment not found");
            }
            
            // 3. Actualizar el pago con la información de MercadoPago
            updatePaymentFromMercadoPago(payment, mpPayment);
            
            // 4. Guardar cambios
            payment = paymentRepository.save(payment);
            
            logger.info("Payment {} updated successfully with status: {}", 
                    payment.getId(), payment.getStatus());
            
            return payment;
            
        } catch (MPException | MPApiException e) {
            logger.error("Error querying MercadoPago payment {}: {}", 
                    mercadopagoPaymentId, e.getMessage(), e);
            throw new PaymentWebhookException("Failed to process webhook: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca un pago por el ID de MercadoPago o la referencia externa (Order ID)
     */
    private Payment findPaymentByReference(Long mercadopagoPaymentId, String externalReference) {
        // Primero intentar por MercadoPago Payment ID
        if (mercadopagoPaymentId != null) {
            Optional<Payment> payment = paymentRepository.findByMercadopagoPaymentId(mercadopagoPaymentId);
            if (payment.isPresent()) {
                return payment.get();
            }
        }
        
        // Si no se encuentra, buscar por Order ID (external reference)
        if (externalReference != null) {
            try {
                Long orderId = Long.parseLong(externalReference);
                Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
                if (payment.isPresent()) {
                    return payment.get();
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid external reference format: {}", externalReference);
            }
        }
        
        return null;
    }
    
    /**
     * Actualiza el pago con la información de MercadoPago
     */
    private void updatePaymentFromMercadoPago(Payment payment, 
            com.mercadopago.resources.payment.Payment mpPayment) {
        
        // Actualizar ID de MercadoPago si no estaba guardado
        if (payment.getMercadopagoPaymentId() == null) {
            payment.setMercadopagoPaymentId(mpPayment.getId());
        }
        
        // Mapear estado de MercadoPago a nuestro enum
        Payment.PaymentStatus newStatus = mapMercadoPagoStatus(mpPayment.getStatus());
        
        // Solo actualizar si el estado cambió
        if (payment.getStatus() != newStatus) {
            logger.info("Updating payment {} status from {} to {}", 
                    payment.getId(), payment.getStatus(), newStatus);
            payment.setStatus(newStatus);
        }
        
        // Actualizar información adicional si está disponible
        if (mpPayment.getTransactionDetails() != null && 
                mpPayment.getTransactionDetails().getExternalResourceUrl() != null) {
            payment.setPaymentUrl(mpPayment.getTransactionDetails().getExternalResourceUrl());
        }
    }
    
    /**
     * Mapea el estado de MercadoPago a nuestro enum PaymentStatus
     * 
     * Estados de MercadoPago:
     * - pending: El usuario aún no completó el pago
     * - approved: El pago fue aprobado y acreditado
     * - authorized: El pago fue autorizado pero no capturado todavía
     * - in_process: El pago está en revisión
     * - in_mediation: El usuario inició una disputa
     * - rejected: El pago fue rechazado
     * - cancelled: El pago fue cancelado por alguna de las partes
     * - refunded: El pago fue devuelto al usuario
     * - charged_back: Se hizo un contracargo en la tarjeta de crédito
     */
    private Payment.PaymentStatus mapMercadoPagoStatus(String mpStatus) {
        if (mpStatus == null) {
            return Payment.PaymentStatus.PENDING;
        }
        
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> Payment.PaymentStatus.CAPTURED;
            case "authorized" -> Payment.PaymentStatus.AUTHORIZED;
            case "rejected", "cancelled", "refunded", "charged_back" -> Payment.PaymentStatus.FAILED;
            default -> Payment.PaymentStatus.PENDING;
        };
    }
    
    /**
     * Verifica si un pago está aprobado/capturado
     */
    public boolean isPaymentApproved(Payment payment) {
        return payment != null && payment.getStatus() == Payment.PaymentStatus.CAPTURED;
    }
    
    /**
     * Obtiene un pago por su Order ID
     */
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
    }
    
    /**
     * Excepción personalizada para errores en la creación de intenciones de pago
     */
    public static class PaymentIntentException extends RuntimeException {
        public PaymentIntentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Excepción cuando no se encuentra un pago
     */
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Excepción para errores al procesar webhooks
     */
    public static class PaymentWebhookException extends RuntimeException {
        public PaymentWebhookException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
