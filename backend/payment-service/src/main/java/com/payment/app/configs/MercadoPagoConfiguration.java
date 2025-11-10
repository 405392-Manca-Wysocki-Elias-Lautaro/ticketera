package com.payment.app.configs;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoConfiguration.class);
    
    @Value("${mercadopago.access-token}")
    private String accessToken;
    
    @PostConstruct
    public void init() {
        logger.info("Initializing Mercado Pago configuration");
        MercadoPagoConfig.setAccessToken(accessToken);
        logger.info("Mercado Pago configuration initialized successfully");
    }
}

