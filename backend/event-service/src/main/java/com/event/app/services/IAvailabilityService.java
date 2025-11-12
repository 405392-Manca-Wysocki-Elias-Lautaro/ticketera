package com.event.app.services;

import java.util.UUID;

public interface IAvailabilityService {
    /**
     * Calcula las entradas disponibles para un evento
     * @param eventId ID del evento
     * @return número de entradas disponibles
     */
    Integer getAvailableTicketsForEvent(UUID eventId);
    
    /**
     * Calcula las entradas disponibles para un área específica
     * @param areaId ID del área
     * @return número de entradas disponibles
     */
    Integer getAvailableTicketsForArea(UUID areaId);
}

