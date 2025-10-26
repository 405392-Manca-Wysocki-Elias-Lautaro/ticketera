package com.order.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        
        // Configurar timeouts
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setConnectionRequestTimeout(Duration.ofSeconds(5));
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Agregar interceptores si es necesario (logging, auth, etc.)
        // restTemplate.setInterceptors(List.of(new LoggingInterceptor()));
        
        return restTemplate;
    }
}
