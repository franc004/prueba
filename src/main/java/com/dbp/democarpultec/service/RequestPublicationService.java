package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.RequestPublicationRequestDto;
import com.dbp.democarpultec.dto.RequestPublicationResponseDto;
import com.dbp.democarpultec.model.RequestPublication;
import com.dbp.democarpultec.repository.RequestPublicationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestPublicationService {

    private final RequestPublicationRepository requestPublicationRepository;
    private final PublicationService publicationService;
    private final UserService userService;

    public List<RequestPublicationResponseDto> findAll() {
        return requestPublicationRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    public RequestPublicationResponseDto findById(Long id) {
        return toResponseDto(findEntityById(id));
    }

    public RequestPublicationResponseDto create(RequestPublicationRequestDto dto) {
        RequestPublication request = new RequestPublication();
        updateEntity(request, dto);
        return toResponseDto(requestPublicationRepository.save(request));
    }

    public RequestPublicationResponseDto update(Long id, RequestPublicationRequestDto dto) {
        RequestPublication request = findEntityById(id);
        updateEntity(request, dto);
        return toResponseDto(requestPublicationRepository.save(request));
    }

    public void delete(Long id) {
        if (!requestPublicationRepository.existsById(id)) {
            throw new EntityNotFoundException("RequestPublication not found with id " + id);
        }
        requestPublicationRepository.deleteById(id);
    }

    public RequestPublication findEntityById(Long id) {
        return requestPublicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RequestPublication not found with id " + id));
    }

    private void updateEntity(RequestPublication request, RequestPublicationRequestDto dto) {
        request.setPublication(publicationService.findEntityById(dto.getPublicationId()));
        request.setRequester(userService.findEntityById(dto.getRequesterId()));
        request.setRequesterIsDriver(dto.getRequesterIsDriver());
        request.setSeats(dto.getSeats());
        request.setMessage(dto.getMessage());
        request.setPickupPointOrDestine(dto.getPickupPointOrDestine());
        request.setStatus(dto.getStatus());
    }

    private RequestPublicationResponseDto toResponseDto(RequestPublication request) {
        return RequestPublicationResponseDto.builder()
                .id(request.getId())
                .publicationId(request.getPublication().getId())
                .requesterId(request.getRequester().getId())
                .requesterIsDriver(request.getRequesterIsDriver())
                .seats(request.getSeats())
                .message(request.getMessage())
                .pickupPointOrDestine(request.getPickupPointOrDestine())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
