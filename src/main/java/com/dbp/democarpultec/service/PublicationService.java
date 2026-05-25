package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.PublicationRequestDto;
import com.dbp.democarpultec.dto.PublicationResponseDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.repository.PublicationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final UserService userService;

    public List<PublicationResponseDto> findAll() {
        return publicationRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    public PublicationResponseDto findById(Long id) {
        return toResponseDto(findEntityById(id));
    }

    public PublicationResponseDto create(PublicationRequestDto dto) {
        Publication publication = new Publication();
        updateEntity(publication, dto);
        return toResponseDto(publicationRepository.save(publication));
    }

    public PublicationResponseDto update(Long id, PublicationRequestDto dto) {
        Publication publication = findEntityById(id);
        updateEntity(publication, dto);
        return toResponseDto(publicationRepository.save(publication));
    }

    public void delete(Long id) {
        if (!publicationRepository.existsById(id)) {
            throw new EntityNotFoundException("Publication not found with id " + id);
        }
        publicationRepository.deleteById(id);
    }

    public Publication findEntityById(Long id) {
        return publicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publication not found with id " + id));
    }

    private void updateEntity(Publication publication, PublicationRequestDto dto) {
        publication.setFromUTEC(dto.getFromUTEC());
        publication.setDriverToPassenger(dto.getDriverToPassenger());
        publication.setSeats(dto.getSeats());
        publication.setTitulo(dto.getTitulo());
        publication.setDescripcion(dto.getDescripcion());
        publication.setDestinationOrOrigin(dto.getDestinationOrOrigin());
        publication.setDepartureTime(dto.getDepartureTime());
        publication.setAuthor(userService.findEntityById(dto.getAuthorId()));
    }

    private PublicationResponseDto toResponseDto(Publication publication) {
        return PublicationResponseDto.builder()
                .id(publication.getId())
                .fromUTEC(publication.getFromUTEC())
                .driverToPassenger(publication.getDriverToPassenger())
                .seats(publication.getSeats())
                .titulo(publication.getTitulo())
                .descripcion(publication.getDescripcion())
                .destinationOrOrigin(publication.getDestinationOrOrigin())
                .departureTime(publication.getDepartureTime())
                .authorId(publication.getAuthor().getId())
                .rideId(publication.getRide() == null ? null : publication.getRide().getId())
                .build();
    }
}
