package com.order.app.controllers;

import com.order.app.pkg.dtos.CreateOrderRequest;
import com.order.app.pkg.dtos.OrderResponse;
import com.order.app.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
@Tag(name = "Orders", description = "Order management API")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping("/create")
    @Operation(summary = "Create a new order", description = "Creates a new order and processes payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Conflict (e.g., seat already reserved)"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        
        logger.info("Received order creation request for customer: {}", request.getCustomer().getEmail());
        
        try {
            OrderResponse orderResponse = orderService.createOrder(request);
            
            logger.info("Order created successfully: {}", orderResponse.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid order request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (OrderService.OrderCreationException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            
        } catch (Exception e) {
            logger.error("Unexpected error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves an order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        
        logger.debug("Getting order: {}", orderId);
        
        Optional<OrderResponse> order = orderService.getOrder(orderId);
        
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            logger.warn("Order not found: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer", description = "Retrieves all orders for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        
        logger.debug("Getting orders for customer: {}", customerId);
        
        List<OrderResponse> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @RequestBody(required = false) CancelOrderRequest request) {
        
        String reason = request != null ? request.getReason() : "Cancelled by user";
        logger.info("Cancelling order: {} with reason: {}", orderId, reason);
        
        boolean cancelled = orderService.cancelOrder(orderId, reason);
        
        if (cancelled) {
            logger.info("Order cancelled successfully: {}", orderId);
            return ResponseEntity.ok().build();
        } else {
            logger.warn("Failed to cancel order: {}", orderId);
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DTO para cancelación
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CancelOrderRequest {
        private String reason;
    }
}
