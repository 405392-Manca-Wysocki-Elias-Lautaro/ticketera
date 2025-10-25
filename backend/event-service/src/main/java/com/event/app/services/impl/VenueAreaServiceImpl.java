package com.event.app.services.impl;

import com.event.app.dtos.GenerateSeatsRequestDTO;
import com.event.app.dtos.RowConfigurationDTO;
import com.event.app.dtos.VenueAreaDTO;
import com.event.app.models.VenueArea;
import com.event.app.models.VenueSeat;
import com.event.app.entities.VenueAreaEntity;
import com.event.app.entities.VenueSeatEntity;
import com.event.app.exceptions.VenueAreaNotFoundException;
import com.event.app.repositories.VenueAreaRepository;
import com.event.app.repositories.VenueSeatRepository;
import com.event.app.services.IVenueAreaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VenueAreaServiceImpl implements IVenueAreaService {

    private final VenueAreaRepository venueAreaRepository;
    private final VenueSeatRepository venueSeatRepository;
    private final ModelMapper modelMapper;

    public VenueAreaServiceImpl(VenueAreaRepository venueAreaRepository, 
                                VenueSeatRepository venueSeatRepository,
                                ModelMapper modelMapper) {
        this.venueAreaRepository = venueAreaRepository;
        this.venueSeatRepository = venueSeatRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public VenueArea createVenueArea(VenueAreaDTO venueAreaDTO) {
        VenueArea venueArea = modelMapper.map(venueAreaDTO, VenueArea.class);
        VenueAreaEntity entity = modelMapper.map(venueArea, VenueAreaEntity.class);
        VenueAreaEntity saved = venueAreaRepository.save(entity);
        return modelMapper.map(saved, VenueArea.class);
    }

    @Override
    public Optional<VenueArea> getVenueAreaById(UUID id) {
        return venueAreaRepository.findById(id)
                .map(entity -> modelMapper.map(entity, VenueArea.class));
    }

    @Override
    public List<VenueArea> getAllVenueAreas() {
        return venueAreaRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, VenueArea.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<VenueArea> getVenueAreasByVenueId(UUID venueId) {
        return venueAreaRepository.findByVenueIdOrderByPositionAsc(venueId).stream()
                .map(entity -> modelMapper.map(entity, VenueArea.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VenueArea updateVenueArea(UUID id, VenueAreaDTO venueAreaDTO) {
        VenueAreaEntity entity = venueAreaRepository.findById(id)
                .orElseThrow(() -> new VenueAreaNotFoundException(id));

        entity.setName(venueAreaDTO.getName());
        entity.setIsGeneralAdmission(venueAreaDTO.getIsGeneralAdmission());
        entity.setCapacity(venueAreaDTO.getCapacity());
        entity.setPosition(venueAreaDTO.getPosition());

        VenueAreaEntity updated = venueAreaRepository.save(entity);
        return modelMapper.map(updated, VenueArea.class);
    }

    @Override
    @Transactional
    public void deleteVenueArea(UUID id) {
        if (!venueAreaRepository.existsById(id)) {
            throw new VenueAreaNotFoundException(id);
        }
        // Primero eliminar todos los asientos asociados
        venueSeatRepository.deleteByVenueAreaId(id);
        // Luego eliminar el área
        venueAreaRepository.deleteById(id);
    }

    @Override
    public List<VenueSeat> getSeatsByVenueAreaId(UUID venueAreaId) {
        // Verificar que el área existe
        if (!venueAreaRepository.existsById(venueAreaId)) {
            throw new VenueAreaNotFoundException(venueAreaId);
        }

        return venueSeatRepository.findByVenueAreaId(venueAreaId).stream()
                .map(entity -> modelMapper.map(entity, VenueSeat.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<VenueSeat> generateSeats(UUID venueAreaId, GenerateSeatsRequestDTO request) {
        // Verificar que el área existe
        VenueAreaEntity venueArea = venueAreaRepository.findById(venueAreaId)
                .orElseThrow(() -> new VenueAreaNotFoundException(venueAreaId));

        // Verificar que no sea un área de admisión general
        if (venueArea.getIsGeneralAdmission()) {
            throw new IllegalArgumentException("No se pueden generar asientos para un área de admisión general");
        }

        // Validar que se proporcionen configuraciones de filas
        if (request.getRows() == null || request.getRows().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos una configuración de fila");
        }

        // Eliminar asientos existentes
        venueSeatRepository.deleteByVenueAreaId(venueAreaId);

        List<VenueSeatEntity> seats = new ArrayList<>();

        // Generar asientos para cada configuración de fila
        int rowCounter = 1; // Contador para generar números de fila automáticamente
        
        for (RowConfigurationDTO rowConfig : request.getRows()) {
            int seatsInThisRow = rowConfig.getSeatsPerRow();
            // Todas las filas empiezan desde el asiento número 1
            int startingSeatNumber = 1;

            // Generar asientos para esta fila específica
            for (int seat = 0; seat < seatsInThisRow; seat++) {
                int currentSeatNumber = startingSeatNumber + seat;
                
                VenueSeatEntity seatEntity = new VenueSeatEntity();
                seatEntity.setVenueAreaId(venueAreaId);
                seatEntity.setRowNumber(rowCounter);
                seatEntity.setSeatNumber(currentSeatNumber);
                // Generar label en formato x-t donde x=rowNumber y t=seatNumber
                seatEntity.setLabel(rowCounter + "-" + currentSeatNumber);
                
                seats.add(seatEntity);
            }
            
            rowCounter++; // Incrementar contador para la siguiente fila
        }

        // Guardar todos los asientos
        List<VenueSeatEntity> savedSeats = venueSeatRepository.saveAll(seats);

        // Actualizar la capacidad del área
        venueArea.setCapacity(savedSeats.size());
        venueAreaRepository.save(venueArea);

        return savedSeats.stream()
                .map(entity -> modelMapper.map(entity, VenueSeat.class))
                .collect(Collectors.toList());
    }
}

