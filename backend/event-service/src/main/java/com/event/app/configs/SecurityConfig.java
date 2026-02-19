package com.event.app.configs;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${JWT_PUBLIC_KEY_PATH}")
    private String publicKeyPath;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // 1. Docs & Internal
                .requestMatchers(
                    "/actuator/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/internal/**"
                ).permitAll()

                // 2. Protected Endpoints (Specific rules first)
                .requestMatchers("/my-organization/**").authenticated()
                .requestMatchers("/staff/**").authenticated()
                .requestMatchers("/metrics/**").authenticated()
                .requestMatchers("/{id}/staff/**").authenticated()

                // 3. Public Endpoints
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/categories/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/*").permitAll()

                // 4. Default
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        // ✅ Lee la key PEM y limpia los headers/líneas
        var keyPem = Files.readString(Paths.get(publicKeyPath))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // limpia saltos de línea y espacios

        byte[] decoded = Base64.getDecoder().decode(keyPem);
        var keySpec = new X509EncodedKeySpec(decoded);
        var keyFactory = KeyFactory.getInstance("RSA");
        var key = (RSAPublicKey) keyFactory.generatePublic(keySpec);

        System.out.println("✅ Public key loaded: " + key.getAlgorithm());
        return key;
    }
}

