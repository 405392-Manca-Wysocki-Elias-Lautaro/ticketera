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

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            OrderItemRepository orderItemRepository,
            OrderStatusHistoryRepository statusHistoryRepository,
            PaymentServiceClient paymentServiceClient
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.paymentServiceClient = paymentServiceClient;
    }

    /**
     * Crea una nueva orden y procesa el pago
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Creating new order for customer: {}", request.getCustomer().getEmail());

        try {
            // 1. Validar request
            validateOrderRequest(request);

            // 2. Obtener o crear customer
            Customer customer = getOrCreateCustomer(request.getCustomer());

            // 3. Crear orden
            Order order = createOrderEntity(request, customer);

            // 4. Crear items de la orden
            List<OrderItem> items = createOrderItems(request.getItems(), order);
            order.setItems(items);

            // 5. Calcular total
            order.calculateTotal();

            // 6. Guardar orden
            order = orderRepository.save(order);

            // 7. Crear historial de estado
            createStatusHistory(order, null, OrderStatus.PENDING, null, "Order created");

            // 8. Procesar pago (pasar la descripción del request)
            PaymentResponse paymentResponse = processPayment(order, request.getPaymentDescription());

            // 9. Actualizar orden según resultado del pago
            updateOrderAfterPayment(order, paymentResponse);

            // 10. Extraer la URL de pago para incluirla en la respuesta
            String paymentUrl = paymentResponse != null && paymentResponse.requiresRedirect()
                    ? paymentResponse.getPaymentUrl()
                    : null;

            logger.info("Order created successfully: {} with payment URL: {}", order.getId(), paymentUrl);
            return OrderResponse.fromEntity(order, paymentUrl);

        } catch (Exception e) {
            logger.error("Error creating order for customer {}: {}", request.getCustomer().getEmail(), e.getMessage(), e);
            throw new OrderCreationException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene una orden por ID
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrder(Long orderId) {
        logger.debug("Getting order: {}", orderId);

        return orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .map(OrderResponse::fromEntity);
    }

    /**
     * Obtiene órdenes por customer ID
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        logger.debug("Getting orders for customer: {}", customerId);

        return orderRepository.findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cancela una orden
     */
    public boolean cancelOrder(Long orderId, String reason) {
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
    private void validateOrderRequest(CreateOrderRequest request) {
        // Validar que no exista una orden con la misma referencia externa
        if (request.getExternalReference() != null
                && orderRepository.existsByExternalReferenceAndDeletedAtIsNull(request.getExternalReference())) {
            throw new IllegalArgumentException("Order with external reference already exists: " + request.getExternalReference());
        }

        // Validar items
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            item.validate();

            // Validar que el asiento no esté ya reservado
            if (item.getEventVenueSeatId() != null) {
                if (orderItemRepository.existsByEventVenueSeatIdAndDeletedAtIsNull(item.getEventVenueSeatId())) {
                    throw new IllegalArgumentException("Seat already reserved: " + item.getEventVenueSeatId());
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
                .organizerId(request.getOrganizerId())
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
                    OrderItem item = OrderItem.builder()
                            .order(order)
                            .occurrenceId(itemRequest.getOccurrenceId())
                            .ticketTypeId(itemRequest.getTicketTypeId())
                            .unitPriceCents(itemRequest.getUnitPriceCents())
                            .quantity(itemRequest.getQuantity())
                            .build();

                    if (itemRequest.getEventVenueAreaId() != null) {
                        item.setEventVenueAreaId(itemRequest.getEventVenueAreaId());
                    }

                    if (itemRequest.getEventVenueSeatId() != null) {
                        item.setEventVenueSeatId(itemRequest.getEventVenueSeatId());
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

    private void createStatusHistory(Order order, OrderStatus fromStatus, OrderStatus toStatus, Long changedBy, String note) {
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
