package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.RideRequestDto;
import com.dbp.democarpultec.model.Publication;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
import com.dbp.democarpultec.repository.PublicationRepository;
import com.dbp.democarpultec.repository.RideRepository;
import com.dbp.democarpultec.repository.UserRepository;
import com.dbp.democarpultec.repository.VehicleRepository;
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
public class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RideRepository rideRepository;

    @MockitoBean
    private PublicationRepository publicationRepository;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final LocalDateTime departureTime = LocalDateTime.of(2025, 6, 1, 8, 30);

    private User buildDriver() {
        return User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .build();
    }

    private Vehicle buildVehicle() {
        return Vehicle.builder()
                .id(1L)
                .owner(buildDriver())
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .seats(4)
                .build();
    }

    private Publication buildPublication() {
        return Publication.builder()
                .id(1L)
                .fromUTEC(true)
                .driverToPassenger(true)
                .seats(3)
                .titulo("Viaje a Miraflores")
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .author(buildDriver())
                .build();
    }

    private Ride buildRide() {
        return Ride.builder()
                .id(1L)
                .publication(buildPublication())
                .driver(buildDriver())
                .vehicle(buildVehicle())
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build();
    }

    private RideRequestDto buildRequest() {
        return RideRequestDto.builder()
                .publicationId(1L)
                .driverId(1L)
                .vehicleId(1L)
                .fromUTEC(true)
                .destinationOrOrigin("Miraflores")
                .departureTime(departureTime)
                .build();
    }

    @Test
    void shouldReturnAllRidesWhenRidesExist() throws Exception {
        when(rideRepository.findAll()).thenReturn(List.of(buildRide()));

        mockMvc.perform(get("/api/rides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destinationOrOrigin").value("Miraflores"))
                .andExpect(jsonPath("$[0].fromUTEC").value(true));

        verify(rideRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoRidesExist() throws Exception {
        when(rideRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/rides"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnRideWhenIdExists() throws Exception {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(buildRide()));

        mockMvc.perform(get("/api/rides/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.driverId").value(1))
                .andExpect(jsonPath("$.destinationOrOrigin").value("Miraflores"));

        verify(rideRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenRideNotFound() throws Exception {
        when(rideRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rides/99"))
                .andExpect(status().isNotFound());

        verify(rideRepository).findById(99L);
    }

    @Test
    void shouldCreateRideWhenValidRequest() throws Exception {
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(buildPublication()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildDriver()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(buildVehicle()));
        when(rideRepository.save(any(Ride.class))).thenReturn(buildRide());

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.publicationId").value(1))
                .andExpect(jsonPath("$.destinationOrOrigin").value("Miraflores"));

        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void shouldReturn400WhenRequestIsMissingRequiredFields() throws Exception {
        RideRequestDto invalid = RideRequestDto.builder()
                .destinationOrOrigin("")
                .build();

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(rideRepository, never()).save(any());
    }

    @Test
    void shouldUpdateRideWhenValidRequest() throws Exception {
        Ride updated = Ride.builder()
                .id(1L)
                .publication(buildPublication())
                .driver(User.builder().id(2L).name("Pedro").lastName("Lopez").email("pedro@test.com").build())
                .vehicle(Vehicle.builder().id(2L).owner(buildDriver()).plate("XYZ-999").brand("Honda").model("Civic").seats(5).build())
                .fromUTEC(false)
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(buildRide()));
        when(publicationRepository.findById(1L)).thenReturn(Optional.of(buildPublication()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(User.builder().id(2L).name("Pedro").lastName("Lopez").email("pedro@test.com").build()));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(Vehicle.builder().id(2L).owner(buildDriver()).plate("XYZ-999").brand("Honda").model("Civic").seats(5).build()));
        when(rideRepository.save(any(Ride.class))).thenReturn(updated);

        RideRequestDto req = RideRequestDto.builder()
                .publicationId(1L)
                .driverId(2L)
                .vehicleId(2L)
                .fromUTEC(false)
                .destinationOrOrigin("San Isidro")
                .departureTime(departureTime)
                .build();

        mockMvc.perform(put("/api/rides/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destinationOrOrigin").value("San Isidro"))
                .andExpect(jsonPath("$.fromUTEC").value(false));

        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentRide() throws Exception {
        when(rideRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/rides/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(rideRepository).findById(99L);
    }

    @Test
    void shouldDeleteRideWhenIdExists() throws Exception {
        when(rideRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rideRepository).deleteById(1L);

        mockMvc.perform(delete("/api/rides/1"))
                .andExpect(status().isNoContent());

        verify(rideRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentRide() throws Exception {
        when(rideRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/rides/99"))
                .andExpect(status().isNotFound());

        verify(rideRepository).existsById(99L);
    }
}
