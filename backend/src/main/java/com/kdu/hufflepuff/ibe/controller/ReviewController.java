package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.ReviewRequestDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.ReviewService;
import com.kdu.hufflepuff.ibe.validation.PositiveLong;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/{tenantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{bookingId}")
    public ResponseEntity<String> submitReview(
            @PositiveLong @PathVariable Long bookingId,
            @PositiveLong @PathVariable Long tenantId,
            @RequestBody ReviewRequestDTO reviewRequest) {

        reviewService.addReviewFromDTO(bookingId, reviewRequest);

        return ResponseEntity.ok("Review submitted successfully");
    }
}
