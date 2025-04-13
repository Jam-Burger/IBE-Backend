package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.ReviewRequestDTO;
import com.kdu.hufflepuff.ibe.model.entity.Review;

public interface ReviewService {
    void addReviewFromDTO(Long bookingId, ReviewRequestDTO dto);

    void addReview(Review review);
}

