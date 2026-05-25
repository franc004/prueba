package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RidePassengerRequestDto;
import com.dbp.democarpultec.dto.RidePassengerResponseDto;
import com.dbp.democarpultec.service.RidePassengerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RidePassengerController.class)
public class RidePassengerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RidePassengerService ridePassengerService;

    private RidePassengerResponseDto buildResponse() {
        return RidePassengerResponseDto.builder()
                .id(1L)
                .passengerId(1L)
                .rideId(1L)
                .seatsReserved(2)
                .pickupPoint("Av. Larco 123")
                .build();
    }

    private RidePassengerRequestDto buildRequest() {
        return RidePassengerRequestDto.builder()
                .passengerId(1L)
                .rideId(1L)
                .seatsReserved(2)
                .pickupPoint("Av. Larco 123")
                .build();
    }

    @Test
    void shouldReturnAllRidePassengersWhenPassengersExist() throws Exception {
        when(ridePassengerService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/ride-passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].passengerId").value(1))
                .andExpect(jsonPath("$[0].seatsReserved").value(2));

        verify(ridePassengerService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRidePassengersExist() throws Exception {
        when(ridePassengerService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/ride-passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRidePassengerWhenIdExists() throws Exception {
        when(ridePassengerService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/ride-passengers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rideId").value(1))
                .andExpect(jsonPath("$.pickupPoint").value("Av. Larco 123"));

        verify(ridePassengerService).findById(1L);
    }

    @Test
    void shouldReturn404WhenRidePassengerNotFound() throws Exception {
        when(ridePassengerService.findById(99L)).thenThrow(new EntityNotFoundException("RidePassenger not found with id 99"));

        mockMvc.perform(get("/api/ride-passengers/99"))
                .andExpect(status().isNotFound());

        verify(ridePassengerService).findById(99L);
    }

    @Test
    void shouldCreateRidePassengerWhenValidRequest() throws Exception {
        when(ridePassengerService.create(any(RidePassengerRequestDto.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/ride-passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.passengerId").value(1))
                .andExpect(jsonPath("$.seatsReserved").value(2));

        verify(ridePassengerService).create(any(RidePassengerRequestDto.class));
    }

    @Test
    void shouldReturn400WhenSeatsReservedIsZero() throws Exception {
        RidePassengerRequestDto invalid = RidePassengerRequestDto.builder()
                .passengerId(1L)
                .rideId(1L)
                .seatsReserved(0)
                .build();

        mockMvc.perform(post("/api/ride-passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(ridePassengerService, never()).create(any());
    }

    @Test
    void shouldReturn400WhenPassengerIdOrRideIdIsNull() throws Exception {
        RidePassengerRequestDto invalid = RidePassengerRequestDto.builder()
                .seatsReserved(1)
                .build();

        mockMvc.perform(post("/api/ride-passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(ridePassengerService, never()).create(any());
    }

    @Test
    void shouldUpdateRidePassengerWhenValidRequest() throws Exception {
        RidePassengerResponseDto updated = RidePassengerResponseDto.builder()
                .id(1L)
                .passengerId(1L)
                .rideId(1L)
                .seatsReserved(3)
                .pickupPoint("Av. Benavides 456")
                .build();

        when(ridePassengerService.update(eq(1L), any(RidePassengerRequestDto.class))).thenReturn(updated);

        RidePassengerRequestDto req = RidePassengerRequestDto.builder()
                .passengerId(1L)
                .rideId(1L)
                .seatsReserved(3)
                .pickupPoint("Av. Benavides 456")
                .build();

        mockMvc.perform(put("/api/ride-passengers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatsReserved").value(3))
                .andExpect(jsonPath("$.pickupPoint").value("Av. Benavides 456"));

        verify(ridePassengerService).update(eq(1L), any(RidePassengerRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentRidePassenger() throws Exception {
        when(ridePassengerService.update(eq(99L), any(RidePassengerRequestDto.class))).thenThrow(new EntityNotFoundException("RidePassenger not found with id 99"));

        mockMvc.perform(put("/api/ride-passengers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(ridePassengerService).update(eq(99L), any(RidePassengerRequestDto.class));
    }

    @Test
    void shouldDeleteRidePassengerWhenIdExists() throws Exception {
        doNothing().when(ridePassengerService).delete(1L);

        mockMvc.perform(delete("/api/ride-passengers/1"))
                .andExpect(status().isNoContent());

        verify(ridePassengerService).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentRidePassenger() throws Exception {
        doThrow(new EntityNotFoundException("RidePassenger not found with id 99")).when(ridePassengerService).delete(99L);

        mockMvc.perform(delete("/api/ride-passengers/99"))
                .andExpect(status().isNotFound());

        verify(ridePassengerService).delete(99L);
    }
}