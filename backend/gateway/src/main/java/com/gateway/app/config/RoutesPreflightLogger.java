package com.gateway.app.config;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(GatewayProperties.class)
public class RoutesPreflightLogger {

    private final GatewayProperties props;

    public RoutesPreflightLogger(GatewayProperties props) {
        this.props = props;
    }

    @PostConstruct
    void logAndValidate() {
        if (props.getRoutes() == null || props.getRoutes().isEmpty()) {
            System.out.println("[GATEWAY-PREFLIGHT] No hay rutas en propiedades.");
            return;
        }
        System.out.println("[GATEWAY-PREFLIGHT] Validando " + props.getRoutes().size() + " rutas de propiedades...");
        for (RouteDefinition rd : props.getRoutes()) {
            String id = rd.getId();
            URI uri = rd.getUri(); // puede venir null si quedó mal parseada

            String uriStr = (uri != null) ? uri.toString() : "(null)";
            try {
                // Validación sintáctica estricta (vuelve a parsear por las dudas)
                if (uri == null) {
                    throw new IllegalArgumentException("URI nula");
                }
                // cheques útiles para gateways http(s)
                String scheme = uri.getScheme();
                if (scheme == null || scheme.isBlank()) {
                    throw new IllegalArgumentException("scheme vacío");
                }
                if ((scheme.equals("http") || scheme.equals("https"))) {
                    // para http/https exigimos host presente
                    if (uri.getHost() == null || uri.getHost().isBlank()) {
                        // típicamente pasa con "http:" o "http://"
                        throw new IllegalArgumentException("host vacío (¿URI tipo 'http:' o 'http://'?)");
                    }
                }
                // Si llegamos hasta acá, la consideramos OK
                System.out.println("[ROUTE OK] id=" + id + " uri=" + uriStr);
            } catch (Exception ex) {
                System.err.println("[ROUTE BROKEN] id=" + id + " uri=" + uriStr + " :: " + ex.getMessage());
            }
        }
    }
}
