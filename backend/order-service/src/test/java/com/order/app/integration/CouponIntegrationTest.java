package com.order.app.integration;

import com.order.app.models.Coupon;
import com.order.app.models.DiscountType;
import com.order.app.models.CouponStatus;
import com.order.app.pkg.dtos.CreateCouponRequest;
import com.order.app.pkg.dtos.ValidateCouponRequest;
import com.order.app.pkg.dtos.ValidateCouponResponse;
import com.order.app.repositories.CouponRepository;
import com.order.app.services.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para el sistema de cupones.
 * 
 * Estos tests verifican el flujo completo:
 * - Creación de cupones
 * - Validación de cupones
 * - Aplicación de descuentos
 * - Redención y estadísticas
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Coupon Integration Tests")
class CouponIntegrationTest {
    
    @Autowired
    private CouponService couponService;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Test
    @DisplayName("Flujo completo: crear cupón y validarlo")
    void testFullCouponFlow() {
        // Arrange
        UUID organizerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        // Paso 1: Crear cupón
        CreateCouponRequest createRequest = CreateCouponRequest.builder()
            .code("INTEGRATION10")
            .description("Integration test coupon")
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(10L)
            .currency("ARS")
            .maxUses(100)
            .maxUsesPerCustomer(1)
            .validFrom(LocalDateTime.now().minusHours(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .build();
        
        var createdCoupon = couponService.createCoupon(createRequest, organizerId, userId);
        
        assertNotNull(createdCoupon);
        assertNotNull(createdCoupon.getId());
        assertEquals("INTEGRATION10", createdCoupon.getCode());
        
        // Paso 2: Validar cupón
        ValidateCouponRequest validateRequest = ValidateCouponRequest.builder()
            .code("INTEGRATION10")
            .organizerId(organizerId)
            .eventId(eventId)
            .subtotalCents(10000L)
            .currency("ARS")
            .customerId(customerId)
            .build();
        
        ValidateCouponResponse validateResponse = couponService.validateCouponForCheckout(validateRequest);
        
        assertTrue(validateResponse.isValid());
        assertNotNull(validateResponse.getCoupon());
        assertEquals(1000L, validateResponse.getDiscountCents()); // 10% de 10000
        
        // Paso 3: Verificar que el cupón existe en BD
        var foundCoupon = couponRepository.findById(createdCoupon.getId());
        assertTrue(foundCoupon.isPresent());
        assertEquals(CouponStatus.ACTIVE, foundCoupon.get().getStatus());
    }
    
    @Test
    @DisplayName("Validar cupón - monto mínimo no cumplido")
    void testValidateCoupon_MinimumNotMet() {
        // Arrange
        UUID organizerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        
        CreateCouponRequest createRequest = CreateCouponRequest.builder()
            .code("MINTEST")
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(15L)
            .currency("ARS")
            .minPurchaseAmountCents(20000L) // Mínimo $200
            .validFrom(LocalDateTime.now().minusHours(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .build();
        
        couponService.createCoupon(createRequest, organizerId, userId);
        
        // Act - Intentar validar con monto menor al mínimo
        ValidateCouponRequest validateRequest = ValidateCouponRequest.builder()
            .code("MINTEST")
            .organizerId(organizerId)
            .eventId(eventId)
            .subtotalCents(10000L) // Solo $100
            .currency("ARS")
            .build();
        
        ValidateCouponResponse response = couponService.validateCouponForCheckout(validateRequest);
        
        // Assert
        assertFalse(response.isValid());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("mínima"));
    }
    
    @Test
    @DisplayName("Validar cupón - moneda incorrecta")
    void testValidateCoupon_CurrencyMismatch() {
        // Arrange
        UUID organizerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        
        CreateCouponRequest createRequest = CreateCouponRequest.builder()
            .code("USDONLY")
            .discountType(DiscountType.FIXED_AMOUNT)
            .discountValue(1000L) // $10 USD
            .currency("USD")
            .validFrom(LocalDateTime.now().minusHours(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .build();
        
        couponService.createCoupon(createRequest, organizerId, userId);
        
        // Act - Intentar usar con ARS
        ValidateCouponRequest validateRequest = ValidateCouponRequest.builder()
            .code("USDONLY")
            .organizerId(organizerId)
            .eventId(eventId)
            .subtotalCents(10000L)
            .currency("ARS") // Moneda incorrecta
            .build();
        
        ValidateCouponResponse response = couponService.validateCouponForCheckout(validateRequest);
        
        // Assert
        assertFalse(response.isValid());
        assertNotNull(response.getErrorMessage());
        assertTrue(response.getErrorMessage().contains("USD"));
    }
    
    @Test
    @DisplayName("Crear cupón - código duplicado no permitido")
    void testCreateCoupon_DuplicateCodeNotAllowed() {
        // Arrange
        UUID organizerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        CreateCouponRequest request = CreateCouponRequest.builder()
            .code("DUPLICATE")
            .discountType(DiscountType.PERCENTAGE)
            .discountValue(10L)
            .validFrom(LocalDateTime.now().plusDays(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .build();
        
        // Act - Crear primer cupón
        couponService.createCoupon(request, organizerId, userId);
        
        // Assert - Intentar crear duplicado
        assertThrows(Exception.class, 
            () -> couponService.createCoupon(request, organizerId, userId));
    }
}


