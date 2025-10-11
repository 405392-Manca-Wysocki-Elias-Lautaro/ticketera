package com.event.app.services.impl;

import com.event.app.dtos.OrganizerDTO;
import com.event.app.models.Organizer;
import com.event.app.entities.OrganizerEntity;
import com.event.app.exceptions.OrganizerNotFoundException;
import com.event.app.repositories.OrganizerRepository;
import com.event.app.services.IOrganizerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrganizerServiceImpl implements IOrganizerService {

    private final OrganizerRepository organizerRepository;
    private final ModelMapper modelMapper;

    public OrganizerServiceImpl(OrganizerRepository organizerRepository, ModelMapper modelMapper) {
        this.organizerRepository = organizerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Organizer createOrganizer(OrganizerDTO organizerDTO) {
        Organizer organizer = modelMapper.map(organizerDTO, Organizer.class);

        organizer.setCreatedAt(LocalDateTime.now());
        organizer.setActive(true);
        OrganizerEntity entity = modelMapper.map(organizer, OrganizerEntity.class);
        OrganizerEntity saved = organizerRepository.save(entity);

        return modelMapper.map(saved, Organizer.class);
    }

    @Override
    public Optional<Organizer> getOrganizerById(Long id) {
        return organizerRepository.findById(id)
                .filter(OrganizerEntity::getActive)
                .map(entity -> modelMapper.map(entity, Organizer.class));
    }

    @Override
    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findByActiveTrue().stream()
                .map(entity -> modelMapper.map(entity, Organizer.class))
                .collect(Collectors.toList());
    }

    @Override
    public Organizer updateOrganizer(Long id, OrganizerDTO organizerDTO) {
        OrganizerEntity organizerEntity = organizerRepository.findById(id)
            .filter(OrganizerEntity::getActive)
            .orElseThrow(() -> new OrganizerNotFoundException(id));

        organizerEntity.setName(organizerDTO.getName());
        organizerEntity.setSlug(organizerDTO.getSlug());
        organizerEntity.setContactEmail(organizerDTO.getContactEmail());
        organizerEntity.setPhoneNumber(organizerDTO.getPhoneNumber());

        OrganizerEntity organizerUpdated = organizerRepository.save(organizerEntity);
        return modelMapper.map(organizerUpdated, Organizer.class);
    }

    @Override
    public void deleteOrganizer(Long id) {
        OrganizerEntity organizerEntity = organizerRepository.findById(id)
            .orElseThrow(() -> new OrganizerNotFoundException(id));

        organizerEntity.setActive(false); 
        organizerRepository.save(organizerEntity);
    }
}
