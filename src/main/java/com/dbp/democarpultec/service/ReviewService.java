package com.dbp.democarpultec.service;

import com.dbp.democarpultec.dto.ReviewRequestDto;
import com.dbp.democarpultec.dto.ReviewResponseDto;
import com.dbp.democarpultec.model.Review;
import com.dbp.democarpultec.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RideService rideService;
    private final UserService userService;

    public List<ReviewResponseDto> findAll() {
        return reviewRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    public ReviewResponseDto findById(Long id) {
        return toResponseDto(findEntityById(id));
    }

    public ReviewResponseDto create(ReviewRequestDto dto) {
        Review review = new Review();
        updateEntity(review, dto);
        return toResponseDto(reviewRepository.save(review));
    }

    public ReviewResponseDto update(Long id, ReviewRequestDto dto) {
        Review review = findEntityById(id);
        updateEntity(review, dto);
        return toResponseDto(reviewRepository.save(review));
    }

    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with id " + id);
        }
        reviewRepository.deleteById(id);
    }

    public Review findEntityById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id " + id));
    }

    private void updateEntity(Review review, ReviewRequestDto dto) {
        review.setRide(rideService.findEntityById(dto.getRideId()));
        review.setReviewer(userService.findEntityById(dto.getReviewerId()));
        review.setReviewed(userService.findEntityById(dto.getReviewedId()));
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
    }

    private ReviewResponseDto toResponseDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .rideId(review.getRide().getId())
                .reviewerId(review.getReviewer().getId())
                .reviewedId(review.getReviewed().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
