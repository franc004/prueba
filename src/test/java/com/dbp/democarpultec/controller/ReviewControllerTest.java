package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.ReviewRequestDto;
import com.dbp.democarpultec.dto.ReviewResponseDto;
import com.dbp.democarpultec.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 10, 0);

    private ReviewResponseDto buildResponse() {
        return ReviewResponseDto.builder()
                .id(1L)
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(5)
                .comment("Excelente conductor")
                .createdAt(createdAt)
                .build();
    }

    private ReviewRequestDto buildRequest() {
        return ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(5)
                .comment("Excelente conductor")
                .build();
    }

    @Test
    void shouldReturnAllReviewsWhenReviewsExist() throws Exception {
        when(reviewService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Excelente conductor"));

        verify(reviewService).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoReviewsExist() throws Exception {
        when(reviewService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnReviewWhenIdExists() throws Exception {
        when(reviewService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reviewerId").value(1))
                .andExpect(jsonPath("$.reviewedId").value(2))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService).findById(1L);
    }

    @Test
    void shouldReturn404WhenReviewNotFound() throws Exception {
        when(reviewService.findById(99L)).thenThrow(new EntityNotFoundException("Review not found with id 99"));

        mockMvc.perform(get("/api/reviews/99"))
                .andExpect(status().isNotFound());

        verify(reviewService).findById(99L);
    }

    @Test
    void shouldCreateReviewWhenValidRequest() throws Exception {
        when(reviewService.create(any(ReviewRequestDto.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excelente conductor"));

        verify(reviewService).create(any(ReviewRequestDto.class));
    }

    @Test
    void shouldReturn400WhenRatingExceedsMaximum() throws Exception {
        ReviewRequestDto invalid = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(6)   // @Max(5) → inválido
                .build();

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).create(any());
    }

    @Test
    void shouldReturn400WhenRatingIsBelowMinimum() throws Exception {
        ReviewRequestDto invalid = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(0)
                .build();

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).create(any());
    }

    @Test
    void shouldReturn400WhenRequiredIdsAreNull() throws Exception {
        ReviewRequestDto invalid = ReviewRequestDto.builder()
                .rating(4)
                .build();

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).create(any());
    }

    @Test
    void shouldUpdateReviewWhenValidRequest() throws Exception {
        ReviewResponseDto updated = ReviewResponseDto.builder()
                .id(1L)
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(3)
                .comment("Bien, pero llegó tarde")
                .createdAt(createdAt)
                .build();

        when(reviewService.update(eq(1L), any(ReviewRequestDto.class))).thenReturn(updated);

        ReviewRequestDto req = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(3)
                .comment("Bien, pero llegó tarde")
                .build();

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(3))
                .andExpect(jsonPath("$.comment").value("Bien, pero llegó tarde"));

        verify(reviewService).update(eq(1L), any(ReviewRequestDto.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentReview() throws Exception {
        when(reviewService.update(eq(99L), any(ReviewRequestDto.class))).thenThrow(new EntityNotFoundException("Review not found with id 99"));

        mockMvc.perform(put("/api/reviews/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(reviewService).update(eq(99L), any(ReviewRequestDto.class));
    }

    @Test
    void shouldDeleteReviewWhenIdExists() throws Exception {
        doNothing().when(reviewService).delete(1L);

        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());

        verify(reviewService).delete(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentReview() throws Exception {
        doThrow(new EntityNotFoundException("Review not found with id 99")).when(reviewService).delete(99L);

        mockMvc.perform(delete("/api/reviews/99"))
                .andExpect(status().isNotFound());

        verify(reviewService).delete(99L);
    }
}