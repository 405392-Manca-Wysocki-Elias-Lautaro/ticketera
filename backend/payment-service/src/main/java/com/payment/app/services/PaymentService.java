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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final PreferenceClient preferenceClient;
    
    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.preferenceClient = new PreferenceClient();
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
            
        } catch (MPException | MPApiException e) {
            logger.error("Error creating Mercado Pago preference for order {}: {}", 
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
        
        // Agregar URLs de retorno si están disponibles
        if (request.getMetadata() != null) {
            String returnUrl = request.getMetadata().getReturnUrl();
            String cancelUrl = request.getMetadata().getCancelUrl();
            
            if (returnUrl != null || cancelUrl != null) {
                PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                        .success(returnUrl)
                        .failure(cancelUrl)
                        .pending(returnUrl)
                        .build();
                
                preferenceBuilder.backUrls(backUrls);
                preferenceBuilder.autoReturn("approved");
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
     * Excepción personalizada para errores en la creación de intenciones de pago
     */
    public static class PaymentIntentException extends RuntimeException {
        public PaymentIntentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
