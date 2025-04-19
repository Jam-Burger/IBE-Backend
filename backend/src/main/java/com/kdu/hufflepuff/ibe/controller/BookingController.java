package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.exception.AuthException;
import com.kdu.hufflepuff.ibe.exception.BookingOperationException;
import com.kdu.hufflepuff.ibe.exception.OTPException;
import com.kdu.hufflepuff.ibe.model.dto.in.BookingRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingDetailsDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.BookingSummaryDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.security.JwtService;
import com.kdu.hufflepuff.ibe.service.interfaces.AccessTokenService;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingPdfService;
import com.kdu.hufflepuff.ibe.service.interfaces.BookingService;
import com.kdu.hufflepuff.ibe.service.interfaces.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management API")
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final BookingPdfService bookingPdfService;
    private final AccessTokenService accessTokenService;
    private final OTPService otpService;
    private  final JwtService jwtService;

    @Operation(summary = "Create new booking", description = "Creates a new booking and returns booking details")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid booking request", content = @Content)
    })

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDetailsDTO>> createBooking(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Parameter(description = "Access token for logged in users") @RequestParam(required = false) String accessToken,
        @Parameter(description = "OTP for validation") @RequestParam(required = false) String otp,
        @Parameter(description = "Booking request details") @Valid @RequestBody BookingRequestDTO request) {

        try {
            String billingEmail = request.getFormData().get("billingEmail");
            authorize(billingEmail, otp, accessToken);

            BookingDetailsDTO response = bookingService.createBooking(tenantId, request);
            return ApiResponse.<BookingDetailsDTO>builder()
                .message("Booking created successfully")
                .data(response)
                .statusCode(HttpStatus.CREATED)
                .build()
                .send();
        } finally {
            if (otp != null)
                otpService.deleteOtp(otp);
        }
    }

    @Operation(summary = "Get booking by ID", description = "Returns a booking object by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailsDTO>> getBooking(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Parameter(description = "ID of the booking to retrieve") @PathVariable Long bookingId) {
        return ApiResponse.<BookingDetailsDTO>builder()
            .message("Booking retrieved successfully")
            .data(bookingService.getBookingDetailsById(bookingId))
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @Operation(summary = "Cancel booking", description = "Cancels an existing booking by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Booking cancelled successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found", content = @Content)
    })
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingDetailsDTO>> cancelBooking(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Parameter(description = "Access token for logged in users") @RequestParam(required = false) String accessToken,
        @Parameter(description = "OTP for validation") @RequestParam(required = false) String otp,
        @Parameter(description = "ID of the booking to cancel") @PathVariable Long bookingId) {

        try {
            BookingDetailsDTO bookingDetails = bookingService.getBookingDetailsById(bookingId);
            String billingEmail = bookingDetails.getGuestDetails().getBillingEmail();

            authorize(billingEmail, otp, accessToken);

            BookingDetailsDTO result = bookingService.cancelBooking(bookingId);
            return ApiResponse.<BookingDetailsDTO>builder()
                .message("Booking cancelled successfully")
                .data(result)
                .statusCode(HttpStatus.ACCEPTED)
                .build()
                .send();
        } finally {
            if (otp != null)
                otpService.deleteOtp(otp);
        }
    }

    @Operation(summary = "Send booking PDF", description = "Generates and sends a PDF of the booking details to the customer's email")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking PDF sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Booking not found", content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error sending PDF", content = @Content)
    })
    @PostMapping("/{bookingId}/send-pdf")
    public ResponseEntity<String> sendBookingPdf(
        @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
        @Parameter(description = "ID of the booking to generate PDF for") @PathVariable Long bookingId) {
        bookingPdfService.generateAndSendBookingPdf(bookingId);
        return ResponseEntity.ok("Booking PDF has been sent successfully.");
    }

    private  void  authorize(String email, String accessToken) {
        if (email == null || email.isEmpty()) {
            throw BookingOperationException.guestCreationFailed("Email is required");
        }

        if (accessToken == null) {
            throw new AuthException("Access token is required");
        }

        if (accessToken != null && !accessToken.isEmpty() && !accessTokenService.verifyAccessToken(email, accessToken)) {
            throw new AuthException("Invalid access token");
        }


    }
    private void authorize(String email, String otp, String accessToken) {

        if (email == null || email.isEmpty()) {
            throw BookingOperationException.guestCreationFailed("Email is required");
        }

        if (accessToken == null && otp == null) {
            throw new AuthException("Access token or OTP is required");
        }

        if (accessToken != null && !accessTokenService.verifyAccessToken(email, accessToken)) {
            throw new AuthException("Invalid access token");
        }

        if (otp != null && !otpService.isOTPVerified(email, otp)) {
            throw OTPException.invalidOtp(otp);
        }
    }


    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingSummaryDTO>>> getBookingsByEmail(
            @PathVariable Long tenantId,
            @RequestParam(required = false) String billingEmail,
            @RequestParam(required = false) String accessToken,
            @RequestParam(required = false) String guestToken
    ) {

        if (guestToken != null && !guestToken.isEmpty()) {
            billingEmail = jwtService.extractBillingEmailFromGuestToken(guestToken);
            jwtService.validateGuestToken(guestToken);
        } else {
            authorize(billingEmail, accessToken);
        }

        List<BookingSummaryDTO> bookingDetails = bookingService.getBookingDetailsByEmail(billingEmail);

        return ApiResponse.<List<BookingSummaryDTO>>builder()
                .message("Booking retrieved successfully")
                .data(bookingDetails)
                .statusCode(HttpStatus.OK)
                .build()
                .send();

    }


}