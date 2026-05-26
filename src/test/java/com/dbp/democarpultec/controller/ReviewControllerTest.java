package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.ReviewRequestDto;
import com.dbp.democarpultec.model.Review;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.PublicationRepository;
import com.dbp.democarpultec.repository.ReviewRepository;
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
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @MockitoBean
    private RideRepository rideRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PublicationRepository publicationRepository;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final LocalDateTime createdAt = LocalDateTime.of(2025, 6, 1, 10, 0);

    private User buildReviewer() {
        return User.builder()
                .id(1L)
                .name("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .build();
    }

    private User buildReviewed() {
        return User.builder()
                .id(2L)
                .name("Maria")
                .lastName("Garcia")
                .email("maria@test.com")
                .build();
    }

    private Review buildReview() {
        return Review.builder()
                .id(1L)
                .ride(Ride.builder().id(1L).build())
                .reviewer(buildReviewer())
                .reviewed(buildReviewed())
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
        when(reviewRepository.findAll()).thenReturn(List.of(buildReview()));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Excelente conductor"));

        verify(reviewRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoReviewsExist() throws Exception {
        when(reviewRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnReviewWhenIdExists() throws Exception {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(buildReview()));

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reviewerId").value(1))
                .andExpect(jsonPath("$.reviewedId").value(2))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewRepository).findById(1L);
    }

    @Test
    void shouldReturn404WhenReviewNotFound() throws Exception {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reviews/99"))
                .andExpect(status().isNotFound());

        verify(reviewRepository).findById(99L);
    }

    @Test
    void shouldCreateReviewWhenValidRequest() throws Exception {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(Ride.builder().id(1L).build()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildReviewer()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buildReviewed()));
        when(reviewRepository.save(any(Review.class))).thenReturn(buildReview());

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excelente conductor"));

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldReturn400WhenRatingExceedsMaximum() throws Exception {
        ReviewRequestDto invalid = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(1L)
                .reviewedId(2L)
                .rating(6)
                .build();

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(reviewRepository, never()).save(any());
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

        verify(reviewRepository, never()).save(any());
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

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void shouldUpdateReviewWhenValidRequest() throws Exception {
        Review updated = Review.builder()
                .id(1L)
                .ride(Ride.builder().id(1L).build())
                .reviewer(buildReviewer())
                .reviewed(buildReviewed())
                .rating(3)
                .comment("Bien, pero llegó tarde")
                .createdAt(createdAt)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(buildReview()));
        when(rideRepository.findById(1L)).thenReturn(Optional.of(Ride.builder().id(1L).build()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildReviewer()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(buildReviewed()));
        when(reviewRepository.save(any(Review.class))).thenReturn(updated);

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

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentReview() throws Exception {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/reviews/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());

        verify(reviewRepository).findById(99L);
    }

    @Test
    void shouldDeleteReviewWhenIdExists() throws Exception {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());

        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentReview() throws Exception {
        when(reviewRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/reviews/99"))
                .andExpect(status().isNotFound());

        verify(reviewRepository).existsById(99L);
    }
}
