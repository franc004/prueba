package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.VehicleRequestDto;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.Vehicle;
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
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @MockitoBean
    private UserRepository userRepository;

    private User buildOwner() {
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
                .owner(buildOwner())
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .color("Blanco")
                .seats(4)
                .build();
    }

    private VehicleRequestDto buildRequest() {
        return VehicleRequestDto.builder()
                .ownerId(1L)
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .color("Blanco")
                .seats(4)
                .build();
    }

    @Test
    void shouldReturnAllVehiclesWhenVehiclesExist() throws Exception {
        when(vehicleRepository.findAll()).thenReturn(List.of(buildVehicle()));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].plate").value("ABC-123"))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));

        verify(vehicleRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoVehiclesExist() throws Exception {
        when(vehicleRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnVehicleWhenIdExists() throws Exception {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(buildVehicle()));

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.plate").value("ABC-123"))
                .andExpect(jsonPath("$.seats").value(4));

        verify(vehicleRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenVehicleNotFound() throws Exception {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/vehicles/99"))
                .andExpect(status().isNotFound());

        verify(vehicleRepository).findById(99L);
    }

    @Test
    void shouldCreateVehicleWhenValidRequest() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildOwner()));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(buildVehicle());

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.plate").value("ABC-123"))
                .andExpect(jsonPath("$.ownerId").value(1));

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldReturn400WhenRequestIsMissingRequiredFields() throws Exception {
        VehicleRequestDto invalid = VehicleRequestDto.builder()
                .plate("")
                .brand("")
                .model("")
                .seats(0)
                .build();

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldUpdateVehicleWhenValidRequest() throws Exception {
        Vehicle updated = Vehicle.builder()
                .id(1L)
                .owner(buildOwner())
                .plate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .color("Negro")
                .seats(5)
                .build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(buildVehicle()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildOwner()));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updated);

        VehicleRequestDto req = VehicleRequestDto.builder()
                .ownerId(1L)
                .plate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .color("Negro")
                .seats(5)
                .build();

        mockMvc.perform(put("/api/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value("XYZ-999"))
                .andExpect(jsonPath("$.brand").value("Honda"));

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentVehicle() throws Exception {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/vehicles/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(vehicleRepository).findById(99L);
    }

    @Test
    void shouldDeleteVehicleWhenIdExists() throws Exception {
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentVehicle() throws Exception {
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/vehicles/99"))
                .andExpect(status().isNotFound());

        verify(vehicleRepository).existsById(99L);
    }
}
