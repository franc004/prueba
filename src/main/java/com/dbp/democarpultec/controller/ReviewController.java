package com.dbp.democarpultec.controller;

import com.dbp.democarpultec.dto.ReviewRequestDto;
import com.dbp.democarpultec.dto.ReviewResponseDto;
import com.dbp.democarpultec.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewResponseDto> findAll() {
        return reviewService.findAll();
    }

    @GetMapping("/{id}")
    public ReviewResponseDto findById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(@Valid @RequestBody ReviewRequestDto review) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(review));
    }

    @PutMapping("/{id}")
    public ReviewResponseDto update(@PathVariable Long id, @Valid @RequestBody ReviewRequestDto review) {
        return reviewService.update(id, review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
