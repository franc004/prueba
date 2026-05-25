package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.ReviewRequestDto;
import com.dbp.democarpultec.dto.ReviewResponseDto;
import com.dbp.democarpultec.model.Review;
import com.dbp.democarpultec.model.Ride;
import com.dbp.democarpultec.model.User;
import com.dbp.democarpultec.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RideService rideService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void shouldCreateReviewWhenValidData() {
        ReviewRequestDto dto = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(2L)
                .reviewedId(3L)
                .rating(5)
                .comment("Buen conductor")
                .build();

        Ride ride = new Ride();
        ride.setId(1L);

        User reviewer = new User();
        reviewer.setId(2L);
        reviewer.setName("Carlos");

        User reviewed = new User();
        reviewed.setId(3L);
        reviewed.setName("Juan");

        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setRide(ride);
        savedReview.setReviewer(reviewer);
        savedReview.setReviewed(reviewed);
        savedReview.setRating(5);
        savedReview.setComment("Buen conductor");
        savedReview.setCreatedAt(LocalDateTime.now());

        when(rideService.findEntityById(1L)).thenReturn(ride);
        when(userService.findEntityById(2L)).thenReturn(reviewer);
        when(userService.findEntityById(3L)).thenReturn(reviewed);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponseDto result = reviewService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getRideId());
        assertEquals(2L, result.getReviewerId());
        assertEquals(3L, result.getReviewedId());
        assertEquals(5, result.getRating());
        assertEquals("Buen conductor", result.getComment());

        verify(rideService).findEntityById(1L);
        verify(userService).findEntityById(2L);
        verify(userService).findEntityById(3L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldReturnReviewWhenIdExists() {
        Ride ride = new Ride();
        ride.setId(1L);

        User reviewer = new User();
        reviewer.setId(2L);
        reviewer.setName("Carlos");

        User reviewed = new User();
        reviewed.setId(3L);
        reviewed.setName("Juan");

        Review review = new Review();
        review.setId(1L);
        review.setRide(ride);
        review.setReviewer(reviewer);
        review.setReviewed(reviewed);
        review.setRating(5);
        review.setComment("Buen conductor");
        review.setCreatedAt(LocalDateTime.now());

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewResponseDto result = reviewService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getRideId());
        assertEquals(2L, result.getReviewerId());
        assertEquals(3L, result.getReviewedId());
        assertEquals(5, result.getRating());

        verify(reviewRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenReviewNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.findById(99L);
        });

        verify(reviewRepository).findById(99L);
    }

    @Test
    void shouldUpdateReviewWhenValidData() {
        ReviewRequestDto dto = ReviewRequestDto.builder()
                .rideId(1L)
                .reviewerId(2L)
                .reviewedId(3L)
                .rating(4)
                .comment("Buen pasajero")
                .build();

        Ride ride = new Ride();
        ride.setId(1L);

        User reviewer = new User();
        reviewer.setId(2L);
        reviewer.setName("Carlos");

        User reviewed = new User();
        reviewed.setId(3L);
        reviewed.setName("Juan");

        Review existingReview = new Review();
        existingReview.setId(1L);
        existingReview.setRating(5);

        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setRide(ride);
        updatedReview.setReviewer(reviewer);
        updatedReview.setReviewed(reviewed);
        updatedReview.setRating(4);
        updatedReview.setComment("Buen pasajero");
        updatedReview.setCreatedAt(LocalDateTime.now());

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existingReview));
        when(rideService.findEntityById(1L)).thenReturn(ride);
        when(userService.findEntityById(2L)).thenReturn(reviewer);
        when(userService.findEntityById(3L)).thenReturn(reviewed);
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        ReviewResponseDto result = reviewService.update(1L, dto);

        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("Buen pasajero", result.getComment());
        assertEquals(2L, result.getReviewerId());
        assertEquals(3L, result.getReviewedId());

        verify(reviewRepository).findById(1L);
        verify(rideService).findEntityById(1L);
        verify(userService).findEntityById(2L);
        verify(userService).findEntityById(3L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldDeleteReviewWhenReviewExists() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        reviewService.delete(1L);
        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
    }
}
