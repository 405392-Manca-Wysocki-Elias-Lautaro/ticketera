package com.event.app.services;

import com.event.app.dtos.AreaDTO;
import com.event.app.models.Area;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAreaService {
    Area createArea(AreaDTO areaDTO);
    Optional<Area> getAreaById(UUID id);
    List<Area> getAreasByEventId(UUID eventId);
    List<Area> getAllAreas();
    Area updateArea(UUID id, AreaDTO areaDTO);
    void deleteArea(UUID id);
}

