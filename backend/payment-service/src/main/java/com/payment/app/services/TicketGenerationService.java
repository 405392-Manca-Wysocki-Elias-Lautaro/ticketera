package com.payment.app.services;

import com.payment.app.clients.OrderServiceClient;
import com.payment.app.clients.TicketServiceClient;
import com.payment.app.models.Payment;
import com.payment.app.pkg.dtos.GenerateTicketRequest;
import com.payment.app.pkg.dtos.OrderInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Servicio para coordinar la generación de tickets después de un pago aprobado
 */
@Service
public class TicketGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketGenerationService.class);
    
    private final OrderServiceClient orderServiceClient;
    private final TicketServiceClient ticketServiceClient;
    
    public TicketGenerationService(
            OrderServiceClient orderServiceClient,
            TicketServiceClient ticketServiceClient) {
        this.orderServiceClient = orderServiceClient;
        this.ticketServiceClient = ticketServiceClient;
    }
    
    /**
     * Genera tickets para una orden pagada
     * 
     * @param payment Pago aprobado
     * @return true si se generaron los tickets exitosamente
     */
    public boolean generateTicketsForPayment(Payment payment) {
        logger.info("Starting ticket generation for payment: {}, orderId: {}", 
                payment.getId(), payment.getOrderId());
        
        try {
            // 1. Obtener información de la orden
            Optional<OrderInfoResponse> orderOpt = orderServiceClient.getOrderById(payment.getOrderId());
            
            if (orderOpt.isEmpty()) {
                logger.error("Order {} not found. Cannot generate tickets.", payment.getOrderId());
                return false;
            }
            
            OrderInfoResponse order = orderOpt.get();
            
            // 2. Validar que la orden tenga items
            if (order.getItems() == null || order.getItems().isEmpty()) {
                logger.warn("Order {} has no items. No tickets to generate.", payment.getOrderId());
                return true; // No es un error, simplemente no hay items
            }
            
            // 3. Validar que tengamos el userId
            if (order.getCustomer() == null || order.getCustomer().getUserId() == null) {
                logger.error("Order {} has no customer userId. Cannot generate tickets.", payment.getOrderId());
                return false;
            }
            
            String userId = order.getCustomer().getUserId();
            
            // 4. Generar un ticket por cada item
            boolean allSuccessful = true;
            int successCount = 0;
            
            for (OrderInfoResponse.OrderItemInfo item : order.getItems()) {
                try {
                    // Por ahora, como no tenemos occurrence_id en el order_item,
                    // usamos el event_id. En el futuro, esto debería venir del order
                    GenerateTicketRequest ticketRequest = GenerateTicketRequest.builder()
                            .orderItemId(item.getId()) // UUID como String
                            .occurrenceId(item.getEventId()) // UUID como String (temporalmente usamos eventId)
                            .userId(userId)
                            .price(BigDecimal.valueOf(item.getUnitPriceCents()).divide(BigDecimal.valueOf(100)))
                            .currency(order.getCurrency())
                            .discount(BigDecimal.ZERO)
                            .finalPrice(BigDecimal.valueOf(item.getTotalPriceCents()).divide(BigDecimal.valueOf(100)))
                            .eventStart(OffsetDateTime.now(ZoneOffset.UTC).plusDays(7)) // TODO: Debería venir del evento
                            .eventEnd(null) // TODO: Debería venir del evento
                            .build();
                    
                    boolean success = ticketServiceClient.generateTicket(ticketRequest);
                    
                    if (success) {
                        successCount++;
                        logger.info("✅ Ticket generated for orderItem: {}", item.getId());
                    } else {
                        allSuccessful = false;
                        logger.error("❌ Failed to generate ticket for orderItem: {}", item.getId());
                    }
                    
                } catch (Exception e) {
                    allSuccessful = false;
                    logger.error("Error generating ticket for orderItem {}: {}", 
                            item.getId(), e.getMessage(), e);
                }
            }
            
            logger.info("Ticket generation completed: {} of {} tickets generated successfully", 
                    successCount, order.getItems().size());
            
            return allSuccessful;
            
        } catch (Exception e) {
            logger.error("Error in ticket generation process for payment {}: {}", 
                    payment.getId(), e.getMessage(), e);
            return false;
        }
    }
}

