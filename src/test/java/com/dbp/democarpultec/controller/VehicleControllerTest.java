package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.VehicleRequestDto;
import com.dbp.democarpultec.dto.VehicleResponseDto;
import com.dbp.democarpultec.service.VehicleService;
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

@WebMvcTest(VehicleController.class)
public class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    private VehicleResponseDto buildResponse() {
        return VehicleResponseDto.builder()
                .id(1L)
                .ownerId(1L)
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
        when(vehicleService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].plate").value("ABC-123"))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));

        verify(vehicleService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoVehiclesExist() throws Exception {
        when(vehicleService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnVehicleWhenIdExists() throws Exception {
        when(vehicleService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.plate").value("ABC-123"))
                .andExpect(jsonPath("$.seats").value(4));

        verify(vehicleService).findById(1L);
    }

    @Test
    void shouldReturn404WhenVehicleNotFound() throws Exception {
        when(vehicleService.findById(99L)).thenThrow(new EntityNotFoundException("Vehicle not found with id 99"));

        mockMvc.perform(get("/api/vehicles/99"))
                .andExpect(status().isNotFound());

        verify(vehicleService).findById(99L);
    }

    @Test
    void shouldCreateVehicleWhenValidRequest() throws Exception {
        when(vehicleService.create(any(VehicleRequestDto.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.plate").value("ABC-123"))
                .andExpect(jsonPath("$.ownerId").value(1));

        verify(vehicleService).create(any(VehicleRequestDto.class));
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

        verify(vehicleService, never()).create(any());
    }

    @Test
    void shouldUpdateVehicleWhenValidRequest() throws Exception {
        VehicleResponseDto updated = VehicleResponseDto.builder()
                .id(1L)
                .ownerId(1L)
                .plate("XYZ-999")
                .brand("Honda")
                .model("Civic")
                .color("Negro")
                .seats(5)
                .build();

        when(vehicleService.update(eq(1L), any(VehicleRequestDto.class))).thenReturn(updated);

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

        verify(vehicleService).update(eq(1L), any(VehicleRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentVehicle() throws Exception {
        when(vehicleService.update(eq(99L), any(VehicleRequestDto.class))).thenThrow(new EntityNotFoundException("Vehicle not found with id 99"));

        mockMvc.perform(put("/api/vehicles/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(vehicleService).update(eq(99L), any(VehicleRequestDto.class));
    }

    @Test
    void shouldDeleteVehicleWhenIdExists() throws Exception {
        doNothing().when(vehicleService).delete(1L);

        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentVehicle() throws Exception {
        doThrow(new EntityNotFoundException("Vehicle not found with id 99")).when(vehicleService).delete(99L);

        mockMvc.perform(delete("/api/vehicles/99"))
                .andExpect(status().isNotFound());

        verify(vehicleService).delete(99L);
    }
}