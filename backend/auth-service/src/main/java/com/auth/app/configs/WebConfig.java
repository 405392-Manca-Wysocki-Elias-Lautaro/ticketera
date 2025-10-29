package com.auth.app.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {

                var mapping = registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);

                if ("prod".equalsIgnoreCase(activeProfile)) {
                    // 🌐 Producción: dominios explícitos
                    mapping.allowedOrigins(
                        "https://app.ticketera.com", // TODO: Configurar dominios
                        "https://frontend.ticketera.dev"
                    );
                } else {
                    // 🧪 Desarrollo: permite todos los orígenes
                    mapping.allowedOriginPatterns("*");
                }
            }
        };
    }
}
