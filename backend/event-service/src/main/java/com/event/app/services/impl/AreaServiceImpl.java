package com.event.app.services.impl;

import com.event.app.dtos.AreaDTO;
import com.event.app.models.Area;
import com.event.app.entities.AreaEntity;
import com.event.app.exceptions.AreaNotFoundException;
import com.event.app.repositories.AreaRepository;
import com.event.app.services.IAreaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AreaServiceImpl implements IAreaService {

    private final AreaRepository areaRepository;
    private final ModelMapper modelMapper;

    public AreaServiceImpl(AreaRepository areaRepository, ModelMapper modelMapper) {
        this.areaRepository = areaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Area createArea(AreaDTO areaDTO) {
        Area area = modelMapper.map(areaDTO, Area.class);
        AreaEntity entity = modelMapper.map(area, AreaEntity.class);
        AreaEntity saved = areaRepository.save(entity);
        return modelMapper.map(saved, Area.class);
    }

    @Override
    public Optional<Area> getAreaById(UUID id) {
        return areaRepository.findById(id)
                .map(entity -> modelMapper.map(entity, Area.class));
    }

    @Override
    public List<Area> getAreasByEventId(UUID eventId) {
        return areaRepository.findByEventId(eventId).stream()
                .map(entity -> modelMapper.map(entity, Area.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Area> getAllAreas() {
        return areaRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, Area.class))
                .collect(Collectors.toList());
    }

    @Override
    public Area updateArea(UUID id, AreaDTO areaDTO) {
        AreaEntity areaEntity = areaRepository.findById(id)
            .orElseThrow(() -> new AreaNotFoundException(id));

        areaEntity.setEventId(areaDTO.getEventId());
        areaEntity.setName(areaDTO.getName());
        areaEntity.setIsGeneralAdmission(areaDTO.getIsGeneralAdmission());
        areaEntity.setCapacity(areaDTO.getCapacity());
        areaEntity.setPosition(areaDTO.getPosition());

        AreaEntity areaUpdated = areaRepository.save(areaEntity);
        return modelMapper.map(areaUpdated, Area.class);
    }

    @Override
    public void deleteArea(UUID id) {
        AreaEntity areaEntity = areaRepository.findById(id)
            .orElseThrow(() -> new AreaNotFoundException(id));

        areaRepository.delete(areaEntity);
    }
}

