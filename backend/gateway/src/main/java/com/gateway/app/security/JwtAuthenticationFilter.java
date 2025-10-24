package com.gateway.app.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.app.config.GatewayRoutesProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final TokenValidationService tokenValidationService;
    private final GatewayRoutesProperties routeProps;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        log.info("[JWT FILTER] Path=" + path + " | public=" + isPublicRoute(path));

        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        return tokenValidationService.validateToken(token)
                .flatMap(valid -> chain.filter(exchange))
                .onErrorResume(ex -> unauthorizedResponse(exchange, ex.getMessage()));
    }

    private boolean isPublicRoute(String path) {
        List<String> publicRoutes = routeProps.getPublicRoutes();
        if (publicRoutes == null || publicRoutes.isEmpty()) {
            return false;
        }

        return publicRoutes.stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", message,
                "path", exchange.getRequest().getPath().value(),
                "timestamp", System.currentTimeMillis()
        );

        try {
            byte[] bytes = new ObjectMapper()
                    .writeValueAsString(body)
                    .getBytes(StandardCharsets.UTF_8);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }

}
