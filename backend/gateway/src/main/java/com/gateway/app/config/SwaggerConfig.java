package com.gateway.app.config;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;

@Configuration
public class SwaggerConfig {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties swaggerUiConfigProperties = new SwaggerUiConfigProperties();
        swaggerUiConfigProperties.setUrls(new HashSet<>());
        return swaggerUiConfigProperties;
    }
}
