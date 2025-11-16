package com.payment.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Servicio para validar webhooks de Mercado Pago usando HMAC-SHA256
 * Basado en la documentación oficial:
 * https://www.mercadopago.com.ar/developers/es/docs/checkout-pro/payment-notifications
 */
@Service
public class WebhookValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookValidationService.class);
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    @Value("${mercadopago.webhook-secret:}")
    private String webhookSecret;
    
    /**
     * Valida la firma HMAC del webhook de Mercado Pago
     * 
     * @param xSignature Header x-signature del webhook
     * @param xRequestId Header x-request-id del webhook
     * @param dataId ID del recurso notificado
     * @return true si la firma es válida, false en caso contrario
     */
    public boolean validateWebhookSignature(String xSignature, String xRequestId, String dataId) {
        // Si no hay secret configurado, no podemos validar
        if (webhookSecret == null || webhookSecret.isEmpty()) {
            logger.warn("Webhook secret not configured. Skipping signature validation.");
            return true; // En desarrollo, permitimos sin validación
        }
        
        if (xSignature == null || xSignature.isEmpty()) {
            logger.error("Missing x-signature header");
            return false;
        }
        
        try {
            // Extraer ts y v1 del header x-signature
            String ts = extractValue(xSignature, "ts");
            String hash = extractValue(xSignature, "v1");
            
            if (ts == null || hash == null) {
                logger.error("Invalid x-signature format: {}", xSignature);
                return false;
            }
            
            // Construir el manifest según la documentación
            String manifest = String.format("id:%s;request-id:%s;ts:%s;", dataId, xRequestId, ts);
            
            // Calcular HMAC
            String calculatedHash = calculateHMAC(manifest, webhookSecret);
            
            // Comparar hashes
            boolean isValid = calculatedHash.equals(hash);
            
            if (!isValid) {
                logger.warn("Invalid webhook signature. Expected: {}, Got: {}", hash, calculatedHash);
            } else {
                logger.info("Webhook signature validated successfully");
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("Error validating webhook signature: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Extrae un valor del header x-signature
     * Formato: "ts=1234567890,v1=abc123..."
     */
    private String extractValue(String xSignature, String key) {
        String[] parts = xSignature.split(",");
        for (String part : parts) {
            String[] keyValue = part.trim().split("=", 2);
            if (keyValue.length == 2 && keyValue[0].trim().equals(key)) {
                return keyValue[1].trim();
            }
        }
        return null;
    }
    
    /**
     * Calcula el HMAC-SHA256 de un mensaje con una clave
     */
    private String calculateHMAC(String message, String secret) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        hmac.init(secretKey);
        
        byte[] hashBytes = hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        
        // Convertir a hexadecimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}

