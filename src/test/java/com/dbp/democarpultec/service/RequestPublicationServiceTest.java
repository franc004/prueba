package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.RequestPublicationRequestDto;
import com.dbp.democarpultec.dto.RequestPublicationResponseDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.RequestPublication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.RequestPublicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestPublicationServiceTest {
    @Mock
    private RequestPublicationRepository requestPublicationRepository;

    @Mock
    private PublicationService publicationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RequestPublicationService requestPublicationService;

    @Test
    void shouldCreateRequestPublicationWhenValidData() {
        RequestPublicationRequestDto dto = RequestPublicationRequestDto.builder()
                .publicationId(1L)
                .requesterId(2L)
                .requesterIsDriver(false)
                .seats(2)
                .message("Puedo unirme?")
                .pickupPointOrDestine("San Miguel")
                .status("pending")
                .build();

        Publication publication = new Publication();
        publication.setId(1L);

        User requester = new User();
        requester.setId(2L);
        requester.setName("Carlos");

        RequestPublication savedRequest = new RequestPublication();
        savedRequest.setId(1L);
        savedRequest.setPublication(publication);
        savedRequest.setRequester(requester);
        savedRequest.setRequesterIsDriver(false);
        savedRequest.setSeats(2);
        savedRequest.setMessage("Puedo unirme?");
        savedRequest.setPickupPointOrDestine("San Miguel");
        savedRequest.setStatus("pending");
        savedRequest.setCreatedAt(LocalDateTime.now());

        when(publicationService.findEntityById(1L)).thenReturn(publication);
        when(userService.findEntityById(2L)).thenReturn(requester);
        when(requestPublicationRepository.save(any(RequestPublication.class))).thenReturn(savedRequest);

        RequestPublicationResponseDto result = requestPublicationService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getPublicationId());
        assertEquals(2L, result.getRequesterId());
        assertEquals(2, result.getSeats());
        assertEquals("pending", result.getStatus());

        verify(publicationService).findEntityById(1L);
        verify(userService).findEntityById(2L);
        verify(requestPublicationRepository).save(any(RequestPublication.class));
    }

    @Test
    void shouldReturnRequestPublicationWhenIdExists() {
        Publication publication = new Publication();
        publication.setId(1L);

        User requester = new User();
        requester.setId(2L);
        requester.setName("Carlos");

        RequestPublication request = new RequestPublication();
        request.setId(1L);
        request.setPublication(publication);
        request.setRequester(requester);
        request.setRequesterIsDriver(false);
        request.setSeats(2);
        request.setMessage("Puedo unirme?");
        request.setPickupPointOrDestine("San Miguel");
        request.setStatus("pending");
        request.setCreatedAt(LocalDateTime.now());

        when(requestPublicationRepository.findById(1L)).thenReturn(Optional.of(request));

        RequestPublicationResponseDto result = requestPublicationService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getPublicationId());
        assertEquals(2L, result.getRequesterId());
        assertEquals("pending", result.getStatus());

        verify(requestPublicationRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenRequestPublicationNotFound() {
        when(requestPublicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            requestPublicationService.findById(99L);
        });

        verify(requestPublicationRepository).findById(99L);
    }

    @Test
    void shouldUpdateRequestPublicationWhenValidData() {
        RequestPublicationRequestDto dto = RequestPublicationRequestDto.builder()
                .publicationId(1L)
                .requesterId(2L)
                .requesterIsDriver(true)
                .seats(3)
                .message("Yo conduzco")
                .pickupPointOrDestine("Miraflores")
                .status("accepted")
                .build();

        Publication publication = new Publication();
        publication.setId(1L);

        User requester = new User();
        requester.setId(2L);
        requester.setName("Carlos");

        RequestPublication existingRequest = new RequestPublication();
        existingRequest.setId(1L);
        existingRequest.setStatus("pending");

        RequestPublication updatedRequest = new RequestPublication();
        updatedRequest.setId(1L);
        updatedRequest.setPublication(publication);
        updatedRequest.setRequester(requester);
        updatedRequest.setRequesterIsDriver(true);
        updatedRequest.setSeats(3);
        updatedRequest.setMessage("Yo conduzco");
        updatedRequest.setPickupPointOrDestine("Miraflores");
        updatedRequest.setStatus("accepted");
        updatedRequest.setCreatedAt(LocalDateTime.now());

        when(requestPublicationRepository.findById(1L)).thenReturn(Optional.of(existingRequest));
        when(publicationService.findEntityById(1L)).thenReturn(publication);
        when(userService.findEntityById(2L)).thenReturn(requester);
        when(requestPublicationRepository.save(any(RequestPublication.class))).thenReturn(updatedRequest);

        RequestPublicationResponseDto result = requestPublicationService.update(1L, dto);

        assertNotNull(result);
        assertEquals("accepted", result.getStatus());
        assertEquals(3, result.getSeats());
        assertEquals(2L, result.getRequesterId());

        verify(requestPublicationRepository).findById(1L);
        verify(publicationService).findEntityById(1L);
        verify(userService).findEntityById(2L);
        verify(requestPublicationRepository).save(any(RequestPublication.class));
    }

    @Test
    void shouldDeleteRequestPublicationWhenRequestPublicationExists() {
        when(requestPublicationRepository.existsById(1L)).thenReturn(true);
        requestPublicationService.delete(1L);
        verify(requestPublicationRepository).existsById(1L);
        verify(requestPublicationRepository).deleteById(1L);
    }
}
