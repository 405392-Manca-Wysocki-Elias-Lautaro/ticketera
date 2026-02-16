package com.order.app.services;

import com.order.app.clients.PaymentServiceClient;
import com.order.app.models.*;
import com.order.app.pkg.dtos.*;
import com.order.app.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final CouponService couponService;
    private final CouponValidationService couponValidationService;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            OrderItemRepository orderItemRepository,
            OrderStatusHistoryRepository statusHistoryRepository,
            PaymentServiceClient paymentServiceClient,
            CouponService couponService,
            CouponValidationService couponValidationService
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.couponService = couponService;
        this.couponValidationService = couponValidationService;
    }

    /**
     * Crea una nueva orden y procesa el pago
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Creating new order for customer: {}", request.getCustomer().getEmail());

        // 1. Validar request (lanza IllegalArgumentException si falla - no envolver)
        validateOrderRequest(request);

        try {
            // 2. Obtener o crear customer
            Customer customer = getOrCreateCustomer(request.getCustomer());

            // 3. Crear orden
            Order order = createOrderEntity(request, customer);

            // 4. Crear items de la orden
            List<OrderItem> items = createOrderItems(request.getItems(), order);
            order.setItems(items);

            // 5. Calcular subtotal
            order.calculateTotal();
            long subtotalCents = order.getTotalCents();
            
            // 6. Procesar cupón si existe
            Coupon appliedCoupon = null;
            long discountCents = 0L;
            
            if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
                try {
                    // Obtener cupón
                    appliedCoupon = couponService.getCouponByCode(
                        request.getCouponCode(), 
                        order.getOrganizerId()
                    );
                    
                    // Obtener eventId del primer item (asumiendo todos de un evento)
                    UUID eventId = order.getItems().get(0).getEventId();
                    
                    // Validar y calcular descuento
                    discountCents = couponValidationService.validateAndCalculateDiscount(
                        appliedCoupon,
                        customer.getId(),
                        eventId,
                        subtotalCents,
                        order.getCurrency()
                    );
                    
                    // Aplicar descuento a la orden
                    order.setCouponId(appliedCoupon.getId());
                    order.setDiscountAmountCents(discountCents);
                    order.setTotalCents(subtotalCents - discountCents);
                    
                    logger.info("Cupón {} aplicado a orden. Descuento: {} centavos", 
                               appliedCoupon.getCode(), discountCents);
                    
                } catch (Exception e) {
                    logger.warn("Error al aplicar cupón {}: {}", request.getCouponCode(), e.getMessage());
                    // Si el cupón falla, continuar sin descuento en vez de bloquear la compra
                    logger.info("Continuando sin cupón para la orden");
                    appliedCoupon = null;
                    discountCents = 0L;
                }
            }

            // 7. Guardar orden
            order = orderRepository.save(order);

            // 8. Registrar redención del cupón si fue aplicado
            if (appliedCoupon != null && discountCents > 0) {
                couponService.redeemCoupon(
                    appliedCoupon,
                    order.getId(),
                    customer.getId(),
                    discountCents,
                    subtotalCents
                );
            }

            // 9. Crear historial de estado
            createStatusHistory(order, null, OrderStatus.PENDING, null, "Order created");

            // 10. Procesar pago (pasar la descripción del request)
            PaymentResponse paymentResponse = processPayment(order, request.getPaymentDescription());

            // 11. Actualizar orden según resultado del pago
            updateOrderAfterPayment(order, paymentResponse);

            // 12. Extraer la URL de pago para incluirla en la respuesta
            String paymentUrl = paymentResponse != null && paymentResponse.requiresRedirect()
                    ? paymentResponse.getPaymentUrl()
                    : null;

            logger.info("Order created successfully: {} with payment URL: {}", order.getId(), paymentUrl);
            return OrderResponse.fromEntity(order, paymentUrl);

        } catch (IllegalArgumentException e) {
            // Propagar errores de validación directamente para que el controller devuelva 400
            logger.warn("Validation error creating order: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating order for customer {}: {}", request.getCustomer().getEmail(), e.getMessage(), e);
            throw new OrderCreationException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene una orden por ID
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrder(UUID orderId) {
        logger.debug("Getting order: {}", orderId);

        return orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .map(OrderResponse::fromEntity);
    }

    /**
     * Obtiene órdenes por customer ID
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(UUID customerId) {
        logger.debug("Getting orders for customer: {}", customerId);

        return orderRepository.findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cancela una orden
     */
    public boolean cancelOrder(UUID orderId, String reason) {
        logger.info("Cancelling order: {} with reason: {}", orderId, reason);

        Optional<Order> orderOpt = orderRepository.findByIdAndDeletedAtIsNull(orderId);
        if (orderOpt.isEmpty()) {
            logger.warn("Order not found for cancellation: {}", orderId);
            return false;
        }

        Order order = orderOpt.get();

        if (!order.canBeCancelled()) {
            logger.warn("Order {} cannot be cancelled. Current status: {}", orderId, order.getStatus());
            return false;
        }

        try {
            // Cancelar payment intent si existe
            // TODO: Implementar lógica para obtener payment intent ID

            // Actualizar estado de la orden
            OrderStatus previousStatus = order.getStatus();
            order.markAsRefunded();
            orderRepository.save(order);

            // Crear historial
            createStatusHistory(order, previousStatus, OrderStatus.REFUNDED, null, reason);

            logger.info("Order cancelled successfully: {}", orderId);
            return true;

        } catch (Exception e) {
            logger.error("Error cancelling order {}: {}", orderId, e.getMessage(), e);
            return false;
        }
    }

    // Métodos privados de apoyo
    
    /**
     * Convierte un string a UUID. Si el string es un número, genera un UUID determinístico.
     * Si ya es un UUID válido, lo retorna directamente.
     */
    private UUID parseOrGenerateUUID(String idString) {
        if (idString == null) {
            logger.debug("parseOrGenerateUUID: input is null, returning null");
            return null;
        }
        
        logger.debug("parseOrGenerateUUID: processing '{}'", idString);
        
        try {
            // Intentar parsear como UUID
            UUID result = UUID.fromString(idString);
            logger.debug("parseOrGenerateUUID: successfully parsed as UUID: {}", result);
            return result;
        } catch (IllegalArgumentException e) {
            // Si falla, intentar como número y generar UUID determinístico
            try {
                long id = Long.parseLong(idString);
                // Generar UUID determinístico usando el número como parte menos significativa
                UUID result = new UUID(0L, id);
                logger.debug("parseOrGenerateUUID: generated UUID from number {}: {}", id, result);
                return result;
            } catch (NumberFormatException ex) {
                // Si no es UUID ni número, generar UUID determinístico desde el hash del string
                // Esto permite manejar formatos como "A-4" (fila-asiento)
                long hash = idString.hashCode();
                // Usar hash como parte menos significativa y un namespace fijo como parte más significativa
                UUID result = new UUID(0x0000000000000000L, hash & 0xFFFFFFFFL);
                logger.debug("parseOrGenerateUUID: generated UUID from string hash '{}': {}", idString, result);
                return result;
            }
        }
    }
    
    private void validateOrderRequest(CreateOrderRequest request) {
        // Validar que no exista una orden con la misma referencia externa
        if (request.getExternalReference() != null
                && orderRepository.existsByExternalReferenceAndDeletedAtIsNull(request.getExternalReference())) {
            throw new IllegalArgumentException("Order with external reference already exists: " + request.getExternalReference());
        }

        // Validar items
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            item.validate();

            // Validar que el asiento no esté ya reservado (solo en órdenes PAID o PENDING no expiradas)
            if (item.getVenueSeatId() != null && !item.getVenueSeatId().isBlank()) {
                UUID venueSeatUuid = parseOrGenerateUUID(item.getVenueSeatId());
                if (orderItemRepository.isSeatActivelyReserved(venueSeatUuid)) {
                    throw new IllegalArgumentException("Seat already reserved by another user: " + item.getVenueSeatId());
                }
            }
        }
    }

    private Customer getOrCreateCustomer(CreateOrderRequest.CustomerInfo customerInfo) {
        // Buscar por userId si está presente
        if (customerInfo.getUserId() != null) {
            Optional<Customer> existing = customerRepository.findActiveByUserId(customerInfo.getUserId());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Buscar por email
        Optional<Customer> existing = customerRepository.findActiveByEmail(customerInfo.getEmail());
        if (existing.isPresent()) {
            Customer customer = existing.get();
            // Actualizar información si es necesario
            updateCustomerInfo(customer, customerInfo);
            return customerRepository.save(customer);
        }

        // Crear nuevo customer
        Customer customer = Customer.builder()
                .email(customerInfo.getEmail())
                .firstName(customerInfo.getFirstName())
                .lastName(customerInfo.getLastName())
                .phone(customerInfo.getPhone())
                .userId(customerInfo.getUserId())
                .build();

        return customerRepository.save(customer);
    }

    private void updateCustomerInfo(Customer customer, CreateOrderRequest.CustomerInfo customerInfo) {
        boolean updated = false;

        if (customerInfo.getFirstName() != null && !customerInfo.getFirstName().equals(customer.getFirstName())) {
            customer.setFirstName(customerInfo.getFirstName());
            updated = true;
        }

        if (customerInfo.getLastName() != null && !customerInfo.getLastName().equals(customer.getLastName())) {
            customer.setLastName(customerInfo.getLastName());
            updated = true;
        }

        if (customerInfo.getPhone() != null && !customerInfo.getPhone().equals(customer.getPhone())) {
            customer.setPhone(customerInfo.getPhone());
            updated = true;
        }

        if (customerInfo.getUserId() != null && !customerInfo.getUserId().equals(customer.getUserId())) {
            customer.setUserId(customerInfo.getUserId());
            updated = true;
        }

        if (updated) {
            logger.debug("Updated customer information: {}", customer.getId());
        }
    }

    private Order createOrderEntity(CreateOrderRequest request, Customer customer) {
        Order order = Order.builder()
            .customer(customer)
            .organizerId(parseOrGenerateUUID(request.getOrganizerId()))
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .build();
        
        if (request.getCurrency() != null) {
            order.setCurrency(request.getCurrency());
        }

        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }

        if (request.getExternalReference() != null) {
            order.setExternalReference(request.getExternalReference());
        }

        if (request.getExpiresAt() != null) {
            order.setExpiresAt(request.getExpiresAt());
        }

        return order;
    }

    private List<OrderItem> createOrderItems(List<CreateOrderRequest.OrderItemRequest> itemRequests, Order order) {
        return itemRequests.stream()
            .map(itemRequest -> {
                // Log detallado para debug
                logger.debug("Creating OrderItem: eventId={}, venueAreaId={}, venueSeatId={}, ticketTypeId={}", 
                        itemRequest.getEventId(), itemRequest.getVenueAreaId(), 
                        itemRequest.getVenueSeatId(), itemRequest.getTicketTypeId());
                
                OrderItem item = OrderItem.builder()
                    .order(order)
                    .eventId(parseOrGenerateUUID(itemRequest.getEventId()))
                    .ticketTypeId(parseOrGenerateUUID(itemRequest.getTicketTypeId()))
                    .unitPriceCents(itemRequest.getUnitPriceCents())
                    .quantity(itemRequest.getQuantity())
                    .build();
                
                if (itemRequest.getVenueAreaId() != null && !itemRequest.getVenueAreaId().trim().isEmpty()) {
                    UUID venueAreaUuid = parseOrGenerateUUID(itemRequest.getVenueAreaId());
                    logger.debug("Parsed venueAreaId '{}' to UUID: {}", itemRequest.getVenueAreaId(), venueAreaUuid);
                    item.setVenueAreaId(venueAreaUuid);
                } else {
                    logger.error("VenueAreaId is null or empty for orderItem, this will cause ticket generation to fail");
                    throw new IllegalArgumentException("VenueAreaId is required for all order items");
                }
                
                if (itemRequest.getVenueSeatId() != null) {
                    item.setVenueSeatId(parseOrGenerateUUID(itemRequest.getVenueSeatId()));
                }
                
                // No guardamos aquí - se guardará automáticamente con cascade cuando se guarde la Order
                return item;
            })
            .collect(Collectors.toList());
    }

    /**
     * Procesa el pago de una orden
     *
     * @param order La orden a procesar
     * @param paymentDescription Descripción personalizada para el pago
     * (opcional)
     * @return PaymentResponse con los detalles del pago
     */
    private PaymentResponse processPayment(Order order, String paymentDescription) {
        logger.info("Processing payment for order: {}", order.getId());

        // Crear request de pago
        PaymentRequest paymentRequest = new PaymentRequest(
                order.getId(),
                1L, // TODO: Obtener provider ID de configuración
                order.getTotalCents(),
                order.getCurrency()
        );

        // Agregar metadata
        PaymentRequest.PaymentMetadata metadata = new PaymentRequest.PaymentMetadata();
        metadata.setCustomerEmail(order.getCustomer().getEmail());
        metadata.setCustomerName(order.getCustomer().getFullName());

        // Usar la descripción personalizada del request, o un mensaje genérico si no viene
        // NUNCA exponer el order_id por seguridad
        if (paymentDescription != null && !paymentDescription.trim().isEmpty()) {
            metadata.setDescription(paymentDescription);
        } else {
            // Mensaje genérico y seguro por defecto
            metadata.setDescription("Compra de tickets");
        }

        paymentRequest.setMetadata(metadata);

        // Llamar al payment service
        Optional<PaymentResponse> paymentResponse = paymentServiceClient.createPaymentIntent(paymentRequest);

        if (paymentResponse.isEmpty()) {
            throw new PaymentProcessingException("Failed to create payment intent for order: " + order.getId());
        }

        return paymentResponse.get();
    }

    private void updateOrderAfterPayment(Order order, PaymentResponse paymentResponse) {
        OrderStatus previousStatus = order.getStatus();

        if (paymentResponse.isSuccessful()) {
            order.markAsPaid(paymentResponse.getProviderRef());
            createStatusHistory(order, previousStatus, OrderStatus.PAID, null, "Payment successful");
            logger.info("Order {} marked as paid", order.getId());

        } else if (paymentResponse.isFailed()) {
            order.markAsFailed();
            createStatusHistory(order, previousStatus, OrderStatus.FAILED, null, "Payment failed: " + paymentResponse.getErrorMessage());
            logger.warn("Order {} payment failed: {}", order.getId(), paymentResponse.getErrorMessage());

        } else {
            // Payment pending - mantener estado actual
            logger.info("Order {} payment is pending", order.getId());
        }

        orderRepository.save(order);
    }
    
    private void createStatusHistory(Order order, OrderStatus fromStatus, OrderStatus toStatus, UUID changedBy, String note) {
        OrderStatusHistory history = OrderStatusHistory.create(order, fromStatus, toStatus, changedBy, note);
        statusHistoryRepository.save(history);
    }

    // Excepciones personalizadas
    public static class OrderCreationException extends RuntimeException {

        public OrderCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class PaymentProcessingException extends RuntimeException {

        public PaymentProcessingException(String message) {
            super(message);
        }
    }
}
