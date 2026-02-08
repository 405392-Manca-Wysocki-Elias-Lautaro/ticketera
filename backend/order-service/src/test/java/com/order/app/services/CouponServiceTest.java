package com.order.app.services;

import com.order.app.exceptions.coupon.*;
import com.order.app.models.Coupon;
import com.order.app.models.CouponRedemption;
import com.order.app.pkg.dtos.*;
import com.order.app.repositories.CouponRedemptionRepository;
import com.order.app.repositories.CouponRepository;
import com.order.app.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CouponService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService Tests")
class CouponServiceTest {
    
    @Mock
    private CouponRepository couponRepository;
    
    @Mock
    private CouponRedemptionRepository redemptionRepository;
    
    @Mock
    private CouponValidationService validationService;
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private CouponService couponService;
    
    private UUID organizerId;
    private UUID userId;
    private UUID customerId;
    private Coupon testCoupon;
    
    @BeforeEach
    void setUp() {
        organizerId = UUID.randomUUID();
        userId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        
        testCoupon = Coupon.builder()
            .id(UUID.randomUUID())
            .organizerId(organizerId)
            .code("TEST20")
            .description("Test coupon 20% off")
            .discountType(Coupon.DiscountType.PERCENTAGE)
            .discountValue(20L)
            .currency("ARS")
            .maxUses(100)
            .maxUsesPerCustomer(1)
            .currentUses(0)
            .validFrom(LocalDateTime.now().minusDays(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .status(Coupon.CouponStatus.ACTIVE)
            .createdBy(userId)
            .build();
    }
    
    // ========================================
    // CREATE TESTS
    // ========================================
    
    @Test
    @DisplayName("Crear cupón exitosamente")
    void testCreateCoupon_Success() {
        // Arrange
        CreateCouponRequest request = CreateCouponRequest.builder()
            .code("SUMMER25")
            .description("Summer discount")
            .discountType(Coupon.DiscountType.PERCENTAGE)
            .discountValue(25L)
            .currency("ARS")
            .maxUses(50)
            .validFrom(LocalDateTime.now().plusDays(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .build();
        
        when(couponRepository.existsByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, "SUMMER25"))
            .thenReturn(false);
        when(couponRepository.save(any(Coupon.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        CouponDTO result = couponService.createCoupon(request, organizerId, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals("SUMMER25", result.getCode());
        assertEquals(organizerId, result.getOrganizerId());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }
    
    @Test
    @DisplayName("Crear cupón - código duplicado lanza excepción")
    void testCreateCoupon_DuplicateCode_ThrowsException() {
        // Arrange
        CreateCouponRequest request = CreateCouponRequest.builder()
            .code("DUPLICATE")
            .discountType(Coupon.DiscountType.PERCENTAGE)
            .discountValue(10L)
            .validFrom(LocalDateTime.now().plusDays(1))
            .validUntil(LocalDateTime.now().plusDays(30))
            .build();
        
        when(couponRepository.existsByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, "DUPLICATE"))
            .thenReturn(true);
        
        // Act & Assert
        assertThrows(CouponCodeAlreadyExistsException.class, 
            () -> couponService.createCoupon(request, organizerId, userId));
        verify(couponRepository, never()).save(any(Coupon.class));
    }
    
    // ========================================
    // READ TESTS
    // ========================================
    
    @Test
    @DisplayName("Obtener cupón por ID exitosamente")
    void testGetCouponById_Success() {
        // Arrange
        UUID couponId = testCoupon.getId();
        when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
            .thenReturn(Optional.of(testCoupon));
        
        // Act
        CouponDTO result = couponService.getCouponById(couponId, organizerId);
        
        // Assert
        assertNotNull(result);
        assertEquals(couponId, result.getId());
        assertEquals("TEST20", result.getCode());
    }
    
    @Test
    @DisplayName("Obtener cupón por ID - no encontrado lanza excepción")
    void testGetCouponById_NotFound_ThrowsException() {
        // Arrange
        UUID couponId = UUID.randomUUID();
        when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CouponNotFoundException.class, 
            () -> couponService.getCouponById(couponId, organizerId));
    }
    
    @Test
    @DisplayName("Obtener cupón por código exitosamente")
    void testGetCouponByCode_Success() {
        // Arrange
        when(couponRepository.findByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, "TEST20"))
            .thenReturn(Optional.of(testCoupon));
        
        // Act
        Coupon result = couponService.getCouponByCode("test20", organizerId); // lowercase
        
        // Assert
        assertNotNull(result);
        assertEquals("TEST20", result.getCode());
    }
    
    @Test
    @DisplayName("Listar cupones por organizador")
    void testGetCouponsByOrganizer() {
        // Arrange
        List<Coupon> coupons = Arrays.asList(testCoupon);
        when(couponRepository.findByOrganizerIdAndDeletedAtIsNull(organizerId))
            .thenReturn(coupons);
        
        // Act
        List<CouponDTO> result = couponService.getCouponsByOrganizer(organizerId, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST20", result.get(0).getCode());
    }
    
    // ========================================
    // UPDATE TESTS
    // ========================================
    
    @Test
    @DisplayName("Actualizar cupón exitosamente")
    void testUpdateCoupon_Success() {
        // Arrange
        UUID couponId = testCoupon.getId();
        UpdateCouponRequest request = UpdateCouponRequest.builder()
            .description("Updated description")
            .maxUses(200)
            .build();
        
        when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
            .thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        CouponDTO result = couponService.updateCoupon(couponId, request, organizerId);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        verify(couponRepository, times(1)).save(testCoupon);
    }
    
    @Test
    @DisplayName("Actualizar cupón - sin permiso lanza excepción")
    void testUpdateCoupon_UnauthorizedAccess_ThrowsException() {
        // Arrange
        UUID couponId = testCoupon.getId();
        UUID differentOrganizerId = UUID.randomUUID();
        UpdateCouponRequest request = UpdateCouponRequest.builder()
            .description("Updated")
            .build();
        
        when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
            .thenReturn(Optional.of(testCoupon));
        
        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, 
            () -> couponService.updateCoupon(couponId, request, differentOrganizerId));
        verify(couponRepository, never()).save(any(Coupon.class));
    }
    
    // ========================================
    // DELETE TESTS
    // ========================================
    
    @Test
    @DisplayName("Eliminar cupón (soft delete) exitosamente")
    void testDeleteCoupon_Success() {
        // Arrange
        UUID couponId = testCoupon.getId();
        when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
            .thenReturn(Optional.of(testCoupon));
        when(couponRepository.save(any(Coupon.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        couponService.deleteCoupon(couponId, organizerId);
        
        // Assert
        assertNotNull(testCoupon.getDeletedAt());
        assertEquals(Coupon.CouponStatus.INACTIVE, testCoupon.getStatus());
        verify(couponRepository, times(1)).save(testCoupon);
    }
    
    // ========================================
    // VALIDATION TESTS
    // ========================================
    
    @Test
    @DisplayName("Validar cupón para checkout - exitoso")
    void testValidateCouponForCheckout_Success() {
        // Arrange
        ValidateCouponRequest request = ValidateCouponRequest.builder()
            .code("TEST20")
            .organizerId(organizerId)
            .eventId(UUID.randomUUID())
            .subtotalCents(10000L)
            .currency("ARS")
            .customerId(customerId)
            .build();
        
        when(couponRepository.findByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, "TEST20"))
            .thenReturn(Optional.of(testCoupon));
        
        // No lanzar excepción en validación
        doNothing().when(validationService).validateCoupon(
            any(), any(), any(), any(), any()
        );
        
        // Act
        ValidateCouponResponse response = couponService.validateCouponForCheckout(request);
        
        // Assert
        assertTrue(response.isValid());
        assertNotNull(response.getCoupon());
        assertEquals(2000L, response.getDiscountCents()); // 20% de 10000
    }
    
    @Test
    @DisplayName("Validar cupón para checkout - cupón inválido")
    void testValidateCouponForCheckout_InvalidCoupon() {
        // Arrange
        ValidateCouponRequest request = ValidateCouponRequest.builder()
            .code("INVALID")
            .organizerId(organizerId)
            .eventId(UUID.randomUUID())
            .subtotalCents(10000L)
            .currency("ARS")
            .build();
        
        when(couponRepository.findByOrganizerIdAndCodeAndDeletedAtIsNull(organizerId, "INVALID"))
            .thenReturn(Optional.empty());
        
        // Act
        ValidateCouponResponse response = couponService.validateCouponForCheckout(request);
        
        // Assert
        assertFalse(response.isValid());
        assertNotNull(response.getErrorMessage());
    }
    
    // ========================================
    // REDEMPTION TESTS
    // ========================================
    
    @Test
    @DisplayName("Redimir cupón exitosamente")
    void testRedeemCoupon_Success() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        long discountCents = 2000L;
        long subtotalCents = 10000L;
        
        when(couponRepository.save(any(Coupon.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(redemptionRepository.save(any(CouponRedemption.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        couponService.redeemCoupon(testCoupon, orderId, customerId, discountCents, subtotalCents);
        
        // Assert
        assertEquals(1, testCoupon.getCurrentUses());
        verify(couponRepository, times(1)).save(testCoupon);
        verify(redemptionRepository, times(1)).save(any(CouponRedemption.class));
    }
    
    // ========================================
    // STATS TESTS
    // ========================================
    
    @Test
    @DisplayName("Obtener estadísticas de cupón")
    void testGetCouponStats_Success() {
        // Arrange
        UUID couponId = testCoupon.getId();
        when(couponRepository.findByIdAndDeletedAtIsNull(couponId))
            .thenReturn(Optional.of(testCoupon));
        when(redemptionRepository.countByCouponId(couponId)).thenReturn(10L);
        when(redemptionRepository.countPaidOrdersByCouponId(couponId)).thenReturn(8L);
        when(redemptionRepository.sumDiscountByCouponId(couponId)).thenReturn(20000L);
        when(redemptionRepository.sumRevenueByCouponId(couponId)).thenReturn(100000L);
        when(redemptionRepository.countUniqueCustomersByCouponId(couponId)).thenReturn(8L);
        when(redemptionRepository.getAverageOrderValueByCouponId(couponId)).thenReturn(12500.0);
        
        // Act
        CouponStatsDTO stats = couponService.getCouponStats(couponId, organizerId);
        
        // Assert
        assertNotNull(stats);
        assertEquals(couponId, stats.getCouponId());
        assertEquals("TEST20", stats.getCouponCode());
        assertEquals(10L, stats.getTotalRedemptions());
        assertEquals(8L, stats.getPaidOrders());
        assertEquals(80.0, stats.getConversionRate(), 0.01);
    }
}

