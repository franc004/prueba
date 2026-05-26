package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RidePassengerRequestDto;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.RidePassenger;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.PublicationRepository;
import com.dbp.democarpultec.repository.RidePassengerRepository;
import com.dbp.democarpultec.repository.RideRepository;
import com.dbp.democarpultec.repository.UserRepository;
import com.dbp.democarpultec.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RidePassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RidePassengerRepository ridePassengerRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RideRepository rideRepository;

    @MockitoBean
    private PublicationRepository publicationRepository;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    private User buildPassengerUser() {
        return User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .build();
    }

    private Ride buildRide() {
        return Ride.builder()
                .id(1L)
                .build();
    }

    private RidePassenger buildRidePassenger() {
        return RidePassenger.builder()
                .id(1L)
                .passenger(buildPassengerUser())
                .ride(Ride.builder().id(1L).build())
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
        when(ridePassengerRepository.findAll()).thenReturn(List.of(buildRidePassenger()));

        mockMvc.perform(get("/api/ride-passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].passengerId").value(1))
                .andExpect(jsonPath("$[0].seatsReserved").value(2));

        verify(ridePassengerRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRidePassengersExist() throws Exception {
        when(ridePassengerRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/ride-passengers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRidePassengerWhenIdExists() throws Exception {
        when(ridePassengerRepository.findById(1L)).thenReturn(Optional.of(buildRidePassenger()));

        mockMvc.perform(get("/api/ride-passengers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rideId").value(1))
                .andExpect(jsonPath("$.pickupPoint").value("Av. Larco 123"));

        verify(ridePassengerRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenRidePassengerNotFound() throws Exception {
        when(ridePassengerRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ride-passengers/99"))
                .andExpect(status().isNotFound());

        verify(ridePassengerRepository).findById(99L);
    }

    @Test
    void shouldCreateRidePassengerWhenValidRequest() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildPassengerUser()));
        when(rideRepository.findById(1L)).thenReturn(Optional.of(buildRide()));
        when(ridePassengerRepository.save(any(RidePassenger.class))).thenReturn(buildRidePassenger());

        mockMvc.perform(post("/api/ride-passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.passengerId").value(1))
                .andExpect(jsonPath("$.seatsReserved").value(2));

        verify(ridePassengerRepository).save(any(RidePassenger.class));
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

        verify(ridePassengerRepository, never()).save(any());
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

        verify(ridePassengerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateRidePassengerWhenValidRequest() throws Exception {
        RidePassenger updated = RidePassenger.builder()
                .id(1L)
                .passenger(buildPassengerUser())
                .ride(Ride.builder().id(1L).build())
                .seatsReserved(3)
                .pickupPoint("Av. Benavides 456")
                .build();

        when(ridePassengerRepository.findById(1L)).thenReturn(Optional.of(buildRidePassenger()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildPassengerUser()));
        when(rideRepository.findById(1L)).thenReturn(Optional.of(buildRide()));
        when(ridePassengerRepository.save(any(RidePassenger.class))).thenReturn(updated);

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

        verify(ridePassengerRepository).save(any(RidePassenger.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentRidePassenger() throws Exception {
        when(ridePassengerRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/ride-passengers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(ridePassengerRepository).findById(99L);
    }

    @Test
    void shouldDeleteRidePassengerWhenIdExists() throws Exception {
        when(ridePassengerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ridePassengerRepository).deleteById(1L);

        mockMvc.perform(delete("/api/ride-passengers/1"))
                .andExpect(status().isNoContent());

        verify(ridePassengerRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentRidePassenger() throws Exception {
        when(ridePassengerRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/ride-passengers/99"))
                .andExpect(status().isNotFound());

        verify(ridePassengerRepository).existsById(99L);
    }
}
