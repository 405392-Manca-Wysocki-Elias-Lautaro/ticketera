package com.event.app.services.impl;

import com.event.app.dtos.SeatDTO;
import com.event.app.models.Seat;
import com.event.app.entities.SeatEntity;
import com.event.app.exceptions.SeatNotFoundException;
import com.event.app.repositories.SeatRepository;
import com.event.app.services.ISeatService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements ISeatService {

    private final SeatRepository seatRepository;
    private final ModelMapper modelMapper;

    public SeatServiceImpl(SeatRepository seatRepository, ModelMapper modelMapper) {
        this.seatRepository = seatRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Seat createSeat(SeatDTO seatDTO) {
        Seat seat = modelMapper.map(seatDTO, Seat.class);
        SeatEntity entity = modelMapper.map(seat, SeatEntity.class);
        SeatEntity saved = seatRepository.save(entity);
        return modelMapper.map(saved, Seat.class);
    }

    @Override
    public Optional<Seat> getSeatById(UUID id) {
        return seatRepository.findById(id)
                .map(entity -> modelMapper.map(entity, Seat.class));
    }

    @Override
    public List<Seat> getSeatsByAreaId(UUID areaId) {
        return seatRepository.findByAreaId(areaId).stream()
                .map(entity -> modelMapper.map(entity, Seat.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, Seat.class))
                .collect(Collectors.toList());
    }

    @Override
    public Seat updateSeat(UUID id, SeatDTO seatDTO) {
        SeatEntity seatEntity = seatRepository.findById(id)
            .orElseThrow(() -> new SeatNotFoundException(id));

        seatEntity.setAreaId(seatDTO.getAreaId());
        seatEntity.setSeatNumber(seatDTO.getSeatNumber());
        seatEntity.setRowNumber(seatDTO.getRowNumber());
        seatEntity.setLabel(seatDTO.getLabel());

        SeatEntity seatUpdated = seatRepository.save(seatEntity);
        return modelMapper.map(seatUpdated, Seat.class);
    }

    @Override
    public void deleteSeat(UUID id) {
        SeatEntity seatEntity = seatRepository.findById(id)
            .orElseThrow(() -> new SeatNotFoundException(id));

        seatRepository.delete(seatEntity);
    }
}

