package com.order.app.controllers;

import com.order.app.pkg.dtos.CreateOrderRequest;
import com.order.app.pkg.dtos.OrderResponse;
import com.order.app.pkg.dtos.response.ApiResponse;
import com.order.app.services.OrderService;
import com.order.app.utils.ApiResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
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
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict (e.g., seat already reserved)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        
        logger.info("Received order creation request for customer: {}", request.getCustomer().getEmail());
        
        try {
            OrderResponse orderResponse = orderService.createOrder(request);
            
            logger.info("Order created successfully: {}", orderResponse.getId());
            return ApiResponseFactory.created("Order created successfully", orderResponse);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid order request: {}", e.getMessage());
            return ApiResponseFactory.badRequest(e.getMessage());
            
        } catch (OrderService.OrderCreationException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ApiResponseFactory.internalError("Failed to create order: " + e.getMessage());
            
        } catch (Exception e) {
            logger.error("Unexpected error creating order: {}", e.getMessage(), e);
            return ApiResponseFactory.internalError("Unexpected error occurred while creating order");
        }
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves an order by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        
        logger.debug("Getting order: {}", orderId);
        
        Optional<OrderResponse> order = orderService.getOrder(orderId);
        
        if (order.isPresent()) {
            return ApiResponseFactory.success("Order retrieved successfully", order.get());
        } else {
            logger.warn("Order not found: {}", orderId);
            return ApiResponseFactory.notFound("Order not found with ID: " + orderId);
        }
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer", description = "Retrieves all orders for a specific customer")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long customerId) {
        
        logger.debug("Getting orders for customer: {}", customerId);
        
        List<OrderResponse> orders = orderService.getOrdersByCustomer(customerId);
        return ApiResponseFactory.success("Orders retrieved successfully", orders);
    }
    
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an existing order")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @RequestBody(required = false) CancelOrderRequest request) {
        
        String reason = request != null ? request.getReason() : "Cancelled by user";
        logger.info("Cancelling order: {} with reason: {}", orderId, reason);
        
        boolean cancelled = orderService.cancelOrder(orderId, reason);
        
        if (cancelled) {
            logger.info("Order cancelled successfully: {}", orderId);
            return ApiResponseFactory.success("Order cancelled successfully");
        } else {
            logger.warn("Failed to cancel order: {}", orderId);
            return ApiResponseFactory.badRequest("Order cannot be cancelled or does not exist");
        }
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CancelOrderRequest {
        private String reason;
    }
}
