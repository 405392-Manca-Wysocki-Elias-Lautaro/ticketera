package com.payment.app.controllers;

import com.payment.app.pkg.dtos.CreatePaymentIntentRequest;
import com.payment.app.pkg.dtos.PaymentIntentResponse;
import com.payment.app.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@Tag(name = "Payments", description = "Payment management API with Mercado Pago Checkout Pro")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/intents")
    @Operation(summary = "Create a payment intent", 
               description = "Creates a payment intent with Mercado Pago Checkout Pro and returns the payment URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment intent created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        
        logger.info("Received payment intent request for order: {}", request.getOrderId());
        
        try {
            PaymentIntentResponse response = paymentService.createPaymentIntent(request);
            
            logger.info("Payment intent created successfully: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (PaymentService.PaymentIntentException e) {
            logger.error("Error creating payment intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            
        } catch (Exception e) {
            logger.error("Unexpected error creating payment intent: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

