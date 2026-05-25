package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.PublicationRequestDto;
import com.dbp.democarpultec.dto.PublicationResponseDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.PublicationRepository;
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
public class PublicationServiceTest {
    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PublicationService publicationService;

    @Test
    void shouldCreatePublicationWhenValidData(){
        PublicationRequestDto dto = PublicationRequestDto.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .descripcion("Salida despues de clases")
                .destinationOrOrigin("Miraflores")
                .departureTime(LocalDateTime.now())
                .authorId(1L)
                .build();

        User author = new User();
        author.setId(1L);
        author.setName("Juan");

        Publication savedPublication = new Publication();
        savedPublication.setId(1L);
        savedPublication.setFromUTEC(true);
        savedPublication.setDriverToPassenger(true);
        savedPublication.setSeats(3);
        savedPublication.setTitulo("Viaje a Miraflores");
        savedPublication.setDescripcion("Salida despues de clases");
        savedPublication.setDestinationOrOrigin("Miraflores");
        savedPublication.setDepartureTime(dto.getDepartureTime());
        savedPublication.setAuthor(author);

        when(userService.findEntityById(1L)).thenReturn(author);
        when(publicationRepository.save(any(Publication.class))).thenReturn(savedPublication);

        PublicationResponseDto result = publicationService.create(dto);

        assertNotNull(result);
        assertEquals("Viaje a Miraflores", result.getTitulo());
        assertEquals(3, result.getSeats());
        assertEquals(1L, result.getAuthorId());
        assertEquals("Miraflores", result.getDestinationOrOrigin());

        verify(userService).findEntityById(1L);
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldReturnPublicationWhenIdExists(){
        User author = new User();
        author.setId(1L);
        author.setName("Juan");

        Publication publication = new Publication();
        publication.setId(1L);
        publication.setFromUTEC(true);
        publication.setDriverToPassenger(true);
        publication.setSeats(3);
        publication.setTitulo("Viaje a Miraflores");
        publication.setDescripcion("Salida despues de clases");
        publication.setDestinationOrOrigin("Miraflores");
        publication.setDepartureTime(LocalDateTime.now());
        publication.setAuthor(author);

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(publication));

        PublicationResponseDto result = publicationService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Viaje a Miraflores", result.getTitulo());
        assertEquals(1L, result.getAuthorId());

        verify(publicationRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenPublicationNotFound(){
        when(publicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            publicationService.findById(99L);
        });

        verify(publicationRepository).findById(99L);
    }

    @Test
    void shouldUpdatePublicationWhenValidData(){
        PublicationRequestDto dto = PublicationRequestDto.builder()
                .fromUTEC(false)
                .driverToPassenger(false)
                .seats(2)
                .titulo("Viaje a San Isidro")
                .descripcion("Salida temprano")
                .destinationOrOrigin("San Isidro")
                .departureTime(LocalDateTime.now())
                .authorId(1L)
                .build();

        User author = new User();
        author.setId(1L);
        author.setName("Juan");

        Publication existingPublication = new Publication();
        existingPublication.setId(1L);
        existingPublication.setTitulo("Titulo viejo");

        Publication updatedPublication = new Publication();
        updatedPublication.setId(1L);
        updatedPublication.setFromUTEC(false);
        updatedPublication.setDriverToPassenger(false);
        updatedPublication.setSeats(2);
        updatedPublication.setTitulo("Viaje a San Isidro");
        updatedPublication.setDescripcion("Salida temprano");
        updatedPublication.setDestinationOrOrigin("San Isidro");
        updatedPublication.setDepartureTime(dto.getDepartureTime());
        updatedPublication.setAuthor(author);

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(existingPublication));
        when(userService.findEntityById(1L)).thenReturn(author);
        when(publicationRepository.save(any(Publication.class))).thenReturn(updatedPublication);

        PublicationResponseDto result = publicationService.update(1L, dto);

        assertNotNull(result);
        assertEquals("Viaje a San Isidro", result.getTitulo());
        assertEquals(2, result.getSeats());
        assertEquals("San Isidro", result.getDestinationOrOrigin());

        verify(publicationRepository).findById(1L);
        verify(userService).findEntityById(1L);
        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldDeletePublicationWhenPublicationExists(){
        when(publicationRepository.existsById(1L)).thenReturn(true);
        publicationService.delete(1L);
        verify(publicationRepository).existsById(1L);
        verify(publicationRepository).deleteById(1L);
    }
}
