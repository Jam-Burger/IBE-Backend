package com.kdu.hufflepuff.ibe.service.impl;


import com.kdu.hufflepuff.ibe.model.dto.in.ReviewRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.Review;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.repository.jpa.ReviewRepository;
import com.kdu.hufflepuff.ibe.repository.jpa.RoomTypeRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingService;
import com.kdu.hufflepuff.ibe.service.interfaces.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RoomTypeRepository roomTypeExtensionRepository;
    private final BookingService bookingDetailsService;

    @Override
    public void addReviewFromDTO(Long bookingId, ReviewRequestDTO dto) {
        Review review = Review.builder()
            .bookingId(bookingId)
            .rating(dto.getRating())
            .comment(dto.getComment())
            .build();

        addReview(review);
    }

    @Override
    public void addReview(Review review) {
        reviewRepository.save(review);

        BookingDetailsDTO bookingDetails = bookingDetailsService.getBookingDetailsById(review.getBookingId());
        Long roomTypeId = bookingDetails.getRoomTypeId();

        if (roomTypeId == null) {
            throw new IllegalArgumentException("RoomTypeId not found for booking ID: " + review.getBookingId());
        }

        RoomTypeExtension roomType = roomTypeExtensionRepository.findById(roomTypeId)
            .orElseThrow(() -> new EntityNotFoundException("RoomTypeExtension not found for ID: " + roomTypeId));

        long currentReviewCount = roomType.getNoOfReviews() != null ? roomType.getNoOfReviews() : 0L;
        double currentRating = roomType.getRating() != null ? roomType.getRating() : 0.0;

        long newReviewCount = currentReviewCount + 1;
        double newAverageRating = ((currentRating * currentReviewCount) + review.getRating()) / newReviewCount;

        roomType.setNoOfReviews(newReviewCount);
        roomType.setRating(newAverageRating);
        roomTypeExtensionRepository.save(roomType);
    }
}

