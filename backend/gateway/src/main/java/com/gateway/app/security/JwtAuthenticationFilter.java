package com.gateway.app.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
import com.gateway.app.dto.ApiError;
import com.gateway.app.dto.ApiResponse;

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
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (routeProps.getPublicRoutes() == null) {
            log.error("🚨 Public routes list is NULL! Check GatewayRoutesProperties binding.");
        } else {
            log.debug("📋 Loaded {} public routes: {}", routeProps.getPublicRoutes().size(), routeProps.getPublicRoutes());
        }

        String path = exchange.getRequest().getPath().value();
        log.info("[JWT FILTER] Path={} | public={}", path, isPublicRoute(path));

        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        return tokenValidationService.validateToken(token)
                .flatMap(valid -> chain.filter(exchange))
                .onErrorResume(ex -> writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", ex.getMessage()));
    }

    private boolean isPublicRoute(String path) {
        List<String> publicRoutes = routeProps.getPublicRoutes();
        if (publicRoutes == null || publicRoutes.isEmpty()) {
            return false;
        }
        return publicRoutes.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String code, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            ApiError apiError = ApiError.builder()
                    .status(status.value())
                    .code(code)
                    .message(message)
                    .details(null)
                    .build();

            ApiResponse<ApiError> apiResponse = ApiResponse.<ApiError>builder()
                    .success(false)
                    .message("Error en la solicitud")
                    .data(apiError)
                    .timestamp(java.time.OffsetDateTime.now())
                    .build();

            byte[] bytes = objectMapper.writeValueAsString(apiResponse).getBytes(StandardCharsets.UTF_8);
            var buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));

        } catch (Exception e) {
            log.error("Failed to write error response: {}", e.getMessage());
            return response.setComplete();
        }
    }
}
