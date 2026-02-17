package com.order.app.controllers;

import com.order.app.pkg.dtos.OrderResponse;
import com.order.app.pkg.dtos.response.ApiResponse;
import com.order.app.services.OrderService;
import com.order.app.utils.ApiResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Controller para llamadas internas entre microservicios (sin autenticación JWT).
 * Solo accesible dentro de la red Docker.
 */
@RestController
@RequestMapping("/internal")
public class InternalOrderController {

    private static final Logger logger = LoggerFactory.getLogger(InternalOrderController.class);

    private final OrderService orderService;

    public InternalOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable String orderId) {
        logger.info("Internal request - Getting order: {}", orderId);

        try {
            UUID orderUuid = UUID.fromString(orderId);
            Optional<OrderResponse> order = orderService.getOrder(orderUuid);

            if (order.isPresent()) {
                return ApiResponseFactory.success("Order retrieved successfully", order.get());
            } else {
                logger.warn("Internal request - Order not found: {}", orderId);
                return ApiResponseFactory.notFound("Order not found with ID: " + orderId);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Internal request - Invalid ID format: {}", orderId);
            return ApiResponseFactory.badRequest("Invalid order ID format: " + e.getMessage());
        }
    }
}
