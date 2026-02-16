package com.order.app.controllers;

import com.order.app.models.CouponRedemption;
import com.order.app.pkg.dtos.*;
import com.order.app.pkg.dtos.response.ApiResponse;
import com.order.app.services.CouponService;
import com.order.app.utils.JwtUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de cupones.
 * 
 * Endpoints:
 * - POST /api/orders/coupons - Crear cupón
 * - GET /api/orders/coupons - Listar cupones del organizador
 * - GET /api/orders/coupons/{id} - Obtener cupón por ID
 * - PUT /api/orders/coupons/{id} - Actualizar cupón
 * - DELETE /api/orders/coupons/{id} - Eliminar cupón
 * - POST /api/orders/coupons/validate - Validar cupón (público)
 * - GET /api/orders/coupons/{id}/stats - Estadísticas del cupón
 * - GET /api/orders/coupons/redemptions - Reporte de redenciones
 */
@RestController
@RequestMapping("/coupons")
public class CouponController {
    
    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);
    
    private final CouponService couponService;
    private final JwtUtils jwtUtils;
    
    public CouponController(CouponService couponService, JwtUtils jwtUtils) {
        this.couponService = couponService;
        this.jwtUtils = jwtUtils;
    }
    
    /**
     * 1. POST /api/orders/coupons
     * Crea un nuevo cupón para el organizador autenticado.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CouponDTO>> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        logger.info("POST /api/orders/coupons - Creating coupon: {}", request.getCode());
        
        try {
            UUID organizerId = jwtUtils.getOrganizerId();
            UUID createdBy = jwtUtils.getUserId();
            
            logger.info("OrganizerId: {}, CreatedBy: {}", organizerId, createdBy);
            
            CouponDTO coupon = couponService.createCoupon(request, organizerId, createdBy);
            
            logger.info("Coupon created successfully: {}", coupon.getId());
            
            ApiResponse<CouponDTO> response = ApiResponse.<CouponDTO>builder()
                    .success(true)
                    .message("Cupón creado exitosamente")
                    .data(coupon)
                    .timestamp(OffsetDateTime.now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating coupon: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 2. GET /api/orders/coupons
     * Lista todos los cupones del organizador con filtros opcionales.
     * 
     * Query params:
     * - status: ACTIVE, INACTIVE, EXPIRED, EXHAUSTED
     * - eventId: UUID del evento
     * - search: texto para buscar en código o descripción
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDTO>>> getCoupons(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String search
    ) {
        logger.debug("GET /api/orders/coupons - status={}, eventId={}, search={}", status, eventId, search);
        
        UUID organizerId = jwtUtils.getOrganizerId();
        
        CouponFilters filters = new CouponFilters(status, eventId);
        filters.setSearchText(search);
        
        List<CouponDTO> coupons = couponService.getCouponsByOrganizer(organizerId, filters);
        
        logger.debug("Found {} coupons", coupons.size());
        
        ApiResponse<List<CouponDTO>> response = ApiResponse.<List<CouponDTO>>builder()
                .success(true)
                .message("Cupones obtenidos exitosamente")
                .data(coupons)
                .timestamp(OffsetDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 3. GET /api/orders/coupons/{id}
     * Obtiene un cupón específico por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponDTO>> getCoupon(@PathVariable UUID id) {
        logger.debug("GET /api/orders/coupons/{}", id);
        
        UUID organizerId = jwtUtils.getOrganizerId();
        CouponDTO coupon = couponService.getCouponById(id, organizerId);
        
        ApiResponse<CouponDTO> response = ApiResponse.<CouponDTO>builder()
                .success(true)
                .message("Cupón obtenido exitosamente")
                .data(coupon)
                .timestamp(OffsetDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 4. PUT /api/orders/coupons/{id}
     * Actualiza un cupón existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponDTO>> updateCoupon(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCouponRequest request
    ) {
        logger.info("PUT /api/orders/coupons/{} - Updating coupon", id);
        
        UUID organizerId = jwtUtils.getOrganizerId();
        CouponDTO coupon = couponService.updateCoupon(id, request, organizerId);
        
        logger.info("Coupon updated successfully: {}", id);
        
        ApiResponse<CouponDTO> response = ApiResponse.<CouponDTO>builder()
                .success(true)
                .message("Cupón actualizado exitosamente")
                .data(coupon)
                .timestamp(OffsetDateTime.now())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 5. DELETE /api/orders/coupons/{id}
     * Elimina (soft delete) un cupón.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        logger.info("DELETE /api/orders/coupons/{} - Deleting coupon", id);
        
        UUID organizerId = jwtUtils.getOrganizerId();
        couponService.deleteCoupon(id, organizerId);
        
        logger.info("Coupon deleted successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 6. POST /api/orders/coupons/validate
     * Valida un cupón sin aplicarlo (para preview en checkout).
     * Endpoint público - no requiere autenticación.
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidateCouponResponse> validateCoupon(
            @Valid @RequestBody ValidateCouponRequest request
    ) {
        logger.debug("POST /api/orders/coupons/validate - code: {}", request.getCode());
        
        ValidateCouponResponse response = couponService.validateCouponForCheckout(request);
        
        if (response.isValid()) {
            logger.debug("Coupon {} is valid, discount: {}", request.getCode(), response.getDiscountCents());
            return ResponseEntity.ok(response);
        } else {
            logger.debug("Coupon {} is invalid: {}", request.getCode(), response.getErrorMessage());
            return ResponseEntity.ok(response); // 200 OK con valid=false
        }
    }
    
    /**
     * 7. GET /api/orders/coupons/{id}/stats
     * Obtiene estadísticas detalladas de un cupón.
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<CouponStatsDTO> getCouponStats(@PathVariable UUID id) {
        logger.debug("GET /api/orders/coupons/{}/stats", id);
        
        UUID organizerId = jwtUtils.getOrganizerId();
        CouponStatsDTO stats = couponService.getCouponStats(id, organizerId);
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 8. GET /api/orders/coupons/redemptions
     * Obtiene reporte de redenciones con filtros.
     * 
     * Query params:
     * - startDate: fecha inicio (ISO 8601)
     * - endDate: fecha fin (ISO 8601)
     * - couponId: UUID del cupón específico
     */
    @GetMapping("/redemptions")
    public ResponseEntity<List<CouponRedemption>> getRedemptionsReport(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) UUID couponId
    ) {
        logger.debug("GET /api/orders/coupons/redemptions - startDate={}, endDate={}, couponId={}", 
                    startDate, endDate, couponId);
        
        UUID organizerId = jwtUtils.getOrganizerId();
        
        List<CouponRedemption> redemptions = couponService.getRedemptionsReport(
            organizerId, 
            startDate, 
            endDate, 
            couponId
        );
        
        logger.debug("Found {} redemptions", redemptions.size());
        return ResponseEntity.ok(redemptions);
    }
    
    /**
     * Manejo global de excepciones para este controlador.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Error in CouponController: {}", e.getMessage(), e);
        
        ErrorResponse error = new ErrorResponse(
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * DTO para respuestas de error.
     */
    public record ErrorResponse(String message, int statusCode) {}
}

