package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingPdfService;
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
    private final BookingPdfService bookingPdfService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDetailsDTO>> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        return ApiResponse.<BookingDetailsDTO>builder()
            .message("Booking created successfully")
            .data(bookingService.createBooking(request))
            .statusCode(HttpStatus.CREATED)
            .build()
            .send();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailsDTO>> getBooking(@PathVariable Long bookingId) {
        return ApiResponse.<BookingDetailsDTO>builder()
            .message("Booking retrieved successfully")
            .data(bookingService.getBookingDetailsById(bookingId))
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingDetailsDTO>> cancelBooking(@PathVariable Long bookingId) {
        return ApiResponse.<BookingDetailsDTO>builder()
            .message("Booking cancelled successfully")
            .data(bookingService.cancelBooking(bookingId))
            .statusCode(HttpStatus.ACCEPTED)
            .build()
            .send();
    }

    @PostMapping("/{bookingId}/send-pdf")
    public ResponseEntity<String> sendBookingPdf(
            @PathVariable Long bookingId) {
        bookingPdfService.generateAndSendBookingPdf(bookingId);
        return ResponseEntity.ok("Booking PDF has been sent successfully.");
    }

} 