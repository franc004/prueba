package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.UserRequestDto;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.model.enums.Carreras;
import com.dbp.democarpultec.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    private User buildUser() {
        return User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .phone("999999999")
                .studentCode("U202410032")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(6)
                .rating(4.5)
                .build();
    }

    private UserRequestDto buildRequest() {
        return UserRequestDto.builder()
                .name("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .phone("999999999")
                .studentCode("U202410032")
                .career(Carreras.Ciencia_de_la_Computacion)
                .cycle(6)
                .rating(4.5)
                .build();
    }

    @Test
    void shouldReturnAllUsersWhenUsersExist() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(buildUser()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Juan"))
                .andExpect(jsonPath("$[0].email").value("juan@test.com"));

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnUserWhenIdExists() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.rating").value(4.5));

        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(99L);
    }

    @Test
    void shouldCreateUserWhenValidRequest() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(buildUser());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@test.com"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturn400WhenRequestIsMissingRequiredFields() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .name("")
                .email("no-es-email")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUpdateUserWhenValidRequest() throws Exception {
        User updated = User.builder()
                .id(1L)
                .name("Pedro")
                .lastName("Lopez")
                .email("pedro@test.com")
                .rating(4.0)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser()));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserRequestDto req = UserRequestDto.builder()
                .name("Pedro")
                .lastName("Lopez")
                .email("pedro@test.com")
                .rating(4.0)
                .build();

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@test.com"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUserWhenIdExists() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        when(userRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());

        verify(userRepository).existsById(99L);
    }
}
