package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.graphql.Booking;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/{tenantId}/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        return ApiResponse.<Booking>builder()
            .message("Booking created successfully")
            .data(bookingService.createBooking(request))
            .statusCode(HttpStatus.CREATED)
            .build()
            .send();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Booking>> getBooking(@PathVariable Long bookingId) {
        return ApiResponse.<Booking>builder()
            .message("Booking retrieved successfully")
            .data(bookingService.getBooking(bookingId))
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable Long bookingId) {
        return ApiResponse.<Booking>builder()
            .message("Booking cancelled successfully")
            .data(bookingService.cancelBooking(bookingId))
            .statusCode(HttpStatus.ACCEPTED)
            .build()
            .send();
    }
} 