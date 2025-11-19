package com.event.app.services;

import com.event.app.dtos.OrganizerMetricsDTO;

import java.util.UUID;

/**
 * Servicio para métricas del organizador
 */
public interface IMetricsService {
    
    /**
     * Obtiene todas las métricas de un organizador
     * @param organizerId ID del organizador
     * @return DTO con todas las métricas
     */
    OrganizerMetricsDTO getOrganizerMetrics(UUID organizerId);
}

