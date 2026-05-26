package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.PublicationRequestDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.PublicationRepository;
import com.dbp.democarpultec.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PublicationRepository publicationRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final LocalDateTime departureTime = LocalDateTime.of(2025, 6, 1, 7, 30);

    private User buildAuthor() {
        return User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .build();
    }

    private Publication buildPublication() {
        return Publication.builder()
                .id(1L)
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .descripcion("Salgo puntual")
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .author(buildAuthor())
                .build();
    }

    private PublicationRequestDto buildRequest() {
        return PublicationRequestDto.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .descripcion("Salgo puntual")
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .authorId(1L)
                .build();
    }

    @Test
    void shouldReturnAllPublicationsWhenPublicationsExist() throws Exception {
        when(publicationRepository.findAll()).thenReturn(List.of(buildPublication()));

        mockMvc.perform(get("/api/publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Viaje a Miraflores"))
                .andExpect(jsonPath("$[0].seats").value(3));

        verify(publicationRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoPublicationsExist() throws Exception {
        when(publicationRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnPublicationWhenIdExists() throws Exception {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(buildPublication()));

        mockMvc.perform(get("/api/publications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Viaje a Miraflores"))
                .andExpect(jsonPath("$.fromUTEC").value(true))
                .andExpect(jsonPath("$.authorId").value(1));

        verify(publicationRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenPublicationNotFound() throws Exception {
        when(publicationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/publications/99"))
                .andExpect(status().isNotFound());

        verify(publicationRepository).findById(99L);
    }

    @Test
    void shouldCreatePublicationWhenValidRequest() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildAuthor()));
        when(publicationRepository.save(any(Publication.class))).thenReturn(buildPublication());

        mockMvc.perform(post("/api/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Viaje a Miraflores"))
                .andExpect(jsonPath("$.driverToPassenger").value(true));

        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldReturn400WhenTituloIsBlank() throws Exception {
        PublicationRequestDto invalid = PublicationRequestDto.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(2)
                .titulo("")
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .authorId(1L)
                .build();

        mockMvc.perform(post("/api/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(publicationRepository, never()).save(any());
    }

    @Test
    void shouldReturn400WhenSeatsIsZero() throws Exception {
        PublicationRequestDto invalid = PublicationRequestDto.builder()
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(0)
                .titulo("Viaje")
                .destinationOrOrigin("Barranco")
                .departureTime(departureTime)
                .authorId(1L)
                .build();

        mockMvc.perform(post("/api/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(publicationRepository, never()).save(any());
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreNull() throws Exception {
        PublicationRequestDto invalid = PublicationRequestDto.builder()
                .titulo("Viaje")
                .destinationOrOrigin("Surco")
                .build();

        mockMvc.perform(post("/api/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(publicationRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePublicationWhenValidRequest() throws Exception {
        Publication updated = Publication.builder()
                .id(1L)
                .fromUTEC(false)
                .driverToPassenger(false)
                .seats(2)
                .titulo("Viaje hacia UTEC")
                .descripcion("Recojo en Surco")
                .destinationOrOrigin("Surco")
                .departureTime(departureTime)
                .author(buildAuthor())
                .build();

        when(publicationRepository.findById(1L)).thenReturn(Optional.of(buildPublication()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildAuthor()));
        when(publicationRepository.save(any(Publication.class))).thenReturn(updated);

        PublicationRequestDto req = PublicationRequestDto.builder()
                .fromUTEC(false)
                .driverToPassenger(false)
                .seats(2)
                .titulo("Viaje hacia UTEC")
                .descripcion("Recojo en Surco")
                .destinationOrOrigin("Surco")
                .departureTime(departureTime)
                .authorId(1L)
                .build();

        mockMvc.perform(put("/api/publications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Viaje hacia UTEC"))
                .andExpect(jsonPath("$.fromUTEC").value(false))
                .andExpect(jsonPath("$.seats").value(2));

        verify(publicationRepository).save(any(Publication.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentPublication() throws Exception {
        when(publicationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/publications/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(publicationRepository).findById(99L);
    }

    @Test
    void shouldDeletePublicationWhenIdExists() throws Exception {
        when(publicationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(publicationRepository).deleteById(1L);

        mockMvc.perform(delete("/api/publications/1"))
                .andExpect(status().isNoContent());

        verify(publicationRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentPublication() throws Exception {
        when(publicationRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/publications/99"))
                .andExpect(status().isNotFound());

        verify(publicationRepository).existsById(99L);
    }
}
