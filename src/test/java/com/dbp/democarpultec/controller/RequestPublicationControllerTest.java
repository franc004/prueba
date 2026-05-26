package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RequestPublicationRequestDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.RequestPublication;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.PublicationRepository;
import com.dbp.democarpultec.repository.RequestPublicationRepository;
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
public class RequestPublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestPublicationRepository requestPublicationRepository;

    @MockitoBean
    private PublicationRepository publicationRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 9, 0);

    private User buildRequester() {
        return User.builder()
                .id(2L)
                .name("Maria")
                .lastName("Garcia")
                .email("maria@test.com")
                .build();
    }

    private Publication buildPublication() {
        return Publication.builder()
                .id(1L)
                .author(User.builder().id(1L).name("Juan").lastName("Perez").email("juan@test.com").build())
                .build();
    }

    private RequestPublication buildRequestPublication() {
        return RequestPublication.builder()
                .id(1L)
                .publication(buildPublication())
                .requester(buildRequester())
                .requesterIsDriver(false)
                .seats(2)
                .message("Me interesa el viaje")
                .pickupPointOrDestine("Av. Larco 200")
                .status("pending")
                .createdAt(createdAt)
                .build();
    }

    private RequestPublicationRequestDto buildRequest() {
        return RequestPublicationRequestDto.builder()
                .publicationId(1L)
                .requesterId(2L)
                .requesterIsDriver(false)
                .seats(2)
                .message("Me interesa el viaje")
                .pickupPointOrDestine("Av. Larco 200")
                .build();
    }

    @Test
    void shouldReturnAllRequestPublicationsWhenRequestsExist() throws Exception {
        when(requestPublicationRepository.findAll()).thenReturn(List.of(buildRequestPublication()));

        mockMvc.perform(get("/api/request-publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("pending"))
                .andExpect(jsonPath("$[0].seats").value(2));

        verify(requestPublicationRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestPublicationsExist() throws Exception {
        when(requestPublicationRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/request-publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRequestPublicationWhenIdExists() throws Exception {
        when(requestPublicationRepository.findById(1L)).thenReturn(Optional.of(buildRequestPublication()));

        mockMvc.perform(get("/api/request-publications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requesterId").value(2))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.requesterIsDriver").value(false));

        verify(requestPublicationRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenRequestPublicationNotFound() throws Exception {
        when(requestPublicationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/request-publications/99"))
                .andExpect(status().isNotFound());

        verify(requestPublicationRepository).findById(99L);
    }

    @Test
    void shouldCreateRequestPublicationWhenValidRequest() throws Exception {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(buildPublication()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buildRequester()));
        when(requestPublicationRepository.save(any(RequestPublication.class))).thenReturn(buildRequestPublication());

        mockMvc.perform(post("/api/request-publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.message").value("Me interesa el viaje"));

        verify(requestPublicationRepository).save(any(RequestPublication.class));
    }

    @Test
    void shouldReturn400WhenSeatsIsZero() throws Exception {
        RequestPublicationRequestDto invalid = RequestPublicationRequestDto.builder()
                .publicationId(1L)
                .requesterId(2L)
                .requesterIsDriver(false)
                .seats(0)
                .build();

        mockMvc.perform(post("/api/request-publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(requestPublicationRepository, never()).save(any());
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreNull() throws Exception {
        RequestPublicationRequestDto invalid = RequestPublicationRequestDto.builder()
                .seats(1)
                .build();

        mockMvc.perform(post("/api/request-publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(requestPublicationRepository, never()).save(any());
    }

    @Test
    void shouldUpdateRequestPublicationWhenValidRequest() throws Exception {
        RequestPublication updated = RequestPublication.builder()
                .id(1L)
                .publication(buildPublication())
                .requester(buildRequester())
                .requesterIsDriver(false)
                .seats(1)
                .message("Actualizo mi solicitud")
                .pickupPointOrDestine("Av. Benavides 500")
                .status("accepted")
                .createdAt(createdAt)
                .build();

        when(requestPublicationRepository.findById(1L)).thenReturn(Optional.of(buildRequestPublication()));
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(buildPublication()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buildRequester()));
        when(requestPublicationRepository.save(any(RequestPublication.class))).thenReturn(updated);

        RequestPublicationRequestDto req = RequestPublicationRequestDto.builder()
                .publicationId(1L)
                .requesterId(2L)
                .requesterIsDriver(false)
                .seats(1)
                .message("Actualizo mi solicitud")
                .pickupPointOrDestine("Av. Benavides 500")
                .status("accepted")
                .build();

        mockMvc.perform(put("/api/request-publications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("accepted"))
                .andExpect(jsonPath("$.seats").value(1))
                .andExpect(jsonPath("$.pickupPointOrDestine").value("Av. Benavides 500"));

        verify(requestPublicationRepository).save(any(RequestPublication.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentRequestPublication() throws Exception {
        when(requestPublicationRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/request-publications/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(requestPublicationRepository).findById(99L);
    }

    @Test
    void shouldDeleteRequestPublicationWhenIdExists() throws Exception {
        when(requestPublicationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(requestPublicationRepository).deleteById(1L);

        mockMvc.perform(delete("/api/request-publications/1"))
                .andExpect(status().isNoContent());

        verify(requestPublicationRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentRequestPublication() throws Exception {
        when(requestPublicationRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/request-publications/99"))
                .andExpect(status().isNotFound());

        verify(requestPublicationRepository).existsById(99L);
    }
}
