package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RequestPublicationRequestDto;
import com.dbp.democarpultec.dto.RequestPublicationResponseDto;
import com.dbp.democarpultec.service.RequestPublicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestPublicationController.class)
public class RequestPublicationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RequestPublicationService requestPublicationService;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 9, 0);

    private RequestPublicationResponseDto buildResponse() {
        return RequestPublicationResponseDto.builder()
                .id(1L)
                .publicationId(1L)
                .requesterId(2L)
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
        when(requestPublicationService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/request-publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("pending"))
                .andExpect(jsonPath("$[0].seats").value(2));

        verify(requestPublicationService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRequestPublicationsExist() throws Exception {
        when(requestPublicationService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/request-publications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRequestPublicationWhenIdExists() throws Exception {
        when(requestPublicationService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/request-publications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requesterId").value(2))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.requesterIsDriver").value(false));

        verify(requestPublicationService).findById(1L);
    }

    @Test
    void shouldReturn404WhenRequestPublicationNotFound() throws Exception {
        when(requestPublicationService.findById(99L)).thenThrow(new EntityNotFoundException("RequestPublication not found with id 99"));

        mockMvc.perform(get("/api/request-publications/99"))
                .andExpect(status().isNotFound());

        verify(requestPublicationService).findById(99L);
    }

    @Test
    void shouldCreateRequestPublicationWhenValidRequest() throws Exception {
        when(requestPublicationService.create(any(RequestPublicationRequestDto.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/request-publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.message").value("Me interesa el viaje"));

        verify(requestPublicationService).create(any(RequestPublicationRequestDto.class));
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

        verify(requestPublicationService, never()).create(any());
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

        verify(requestPublicationService, never()).create(any());
    }

    @Test
    void shouldUpdateRequestPublicationWhenValidRequest() throws Exception {
        RequestPublicationResponseDto updated = RequestPublicationResponseDto.builder()
                .id(1L)
                .publicationId(1L)
                .requesterId(2L)
                .requesterIsDriver(false)
                .seats(1)
                .message("Actualizo mi solicitud")
                .pickupPointOrDestine("Av. Benavides 500")
                .status("accepted")
                .createdAt(createdAt)
                .build();

        when(requestPublicationService.update(eq(1L), any(RequestPublicationRequestDto.class))).thenReturn(updated);

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

        verify(requestPublicationService).update(eq(1L), any(RequestPublicationRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentRequestPublication() throws Exception {
        when(requestPublicationService.update(eq(99L), any(RequestPublicationRequestDto.class))).thenThrow(new EntityNotFoundException("RequestPublication not found with id 99"));

        mockMvc.perform(put("/api/request-publications/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(requestPublicationService).update(eq(99L), any(RequestPublicationRequestDto.class));
    }

    @Test
    void shouldDeleteRequestPublicationWhenIdExists() throws Exception {
        doNothing().when(requestPublicationService).delete(1L);

        mockMvc.perform(delete("/api/request-publications/1"))
                .andExpect(status().isNoContent());

        verify(requestPublicationService).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentRequestPublication() throws Exception {
        doThrow(new EntityNotFoundException("RequestPublication not found with id 99")).when(requestPublicationService).delete(99L);

        mockMvc.perform(delete("/api/request-publications/99"))
                .andExpect(status().isNotFound());

        verify(requestPublicationService).delete(99L);
    }
}