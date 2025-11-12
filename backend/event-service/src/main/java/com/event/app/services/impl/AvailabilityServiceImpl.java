package com.event.app.services.impl;

import com.event.app.repositories.AreaRepository;
import com.event.app.repositories.SeatRepository;
import com.event.app.services.IAvailabilityService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio para calcular disponibilidad de entradas
 * 
 * NOTA: En una implementación completa, este servicio consultaría al ticket-service
 * o order-service para obtener las entradas vendidas/reservadas.
 * 
 * Por ahora, retorna la capacidad total como disponible (placeholder).
 */
@Service
public class AvailabilityServiceImpl implements IAvailabilityService {

    private final AreaRepository areaRepository;
    private final SeatRepository seatRepository;

    public AvailabilityServiceImpl(AreaRepository areaRepository, SeatRepository seatRepository) {
        this.areaRepository = areaRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public Integer getAvailableTicketsForEvent(UUID eventId) {
        // TODO: Consultar al ticket-service/order-service para obtener tickets vendidos
        // Por ahora retornamos la capacidad total
        return areaRepository.findByEventId(eventId).stream()
                .mapToInt(area -> {
                    if (area.getCapacity() != null) {
                        return area.getCapacity();
                    }
                    // Si no tiene capacidad definida, contar asientos
                    return seatRepository.findByAreaId(area.getId()).size();
                })
                .sum();
    }

    @Override
    public Integer getAvailableTicketsForArea(UUID areaId) {
        // TODO: Consultar al ticket-service/order-service para obtener tickets vendidos del área
        return areaRepository.findById(areaId)
                .map(area -> {
                    if (area.getCapacity() != null) {
                        return area.getCapacity();
                    }
                    return seatRepository.findByAreaId(area.getId()).size();
                })
                .orElse(0);
    }
}

