package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.GuestSessionDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.security.JwtService;
import com.kdu.hufflepuff.ibe.service.interfaces.OTPService;
import com.kdu.hufflepuff.ibe.validation.PositiveLong;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{tenantId}/otp")
@Tag(name = "OTP", description = "One-Time Password generation and verification API")
public class OTPController {
    private final OTPService otpService;
    private final JwtService jwtService;

    @Operation(
        summary = "Send OTP to email",
        description = "Generates a new OTP and sends it to the provided email address"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "OTP sent successfully",
            content = @Content(mediaType = "text/plain",
                schema = @Schema(type = "string"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid email address",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Error sending OTP",
            content = @Content)
    })
    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(
        @Parameter(description = "ID of the tenant") @PositiveLong @PathVariable Long tenantId,
        @Parameter(description = "Email address to send OTP to", required = true) @RequestParam String email) {
        otpService.generateOtp(email);
        return ResponseEntity.ok("OTP sent to email");
    }

    @Operation(
        summary = "Verify OTP",
        description = "Verifies if the provided OTP is valid for the given email address"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "OTP verified successfully",
            content = @Content(mediaType = "text/plain",
                schema = @Schema(type = "string"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid or expired OTP",
            content = @Content(mediaType = "text/plain",
                schema = @Schema(type = "string")))
    })
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<GuestSessionDTO>> verifyOtp(
        @Parameter(description = "ID of the tenant") @PositiveLong @PathVariable Long tenantId,
        @Parameter(description = "Email address associated with the OTP", required = true) @RequestParam String email,
        @Parameter(description = "One-Time Password to verify", required = true) @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (!isValid) {
            return ApiResponse.<GuestSessionDTO>builder()
                .message("Invalid OTP")
                .statusCode(HttpStatus.UNAUTHORIZED)
                .build()
                .send();
        }

        String guestSessionToken = jwtService.generateGuestJwtToken(email);

        GuestSessionDTO response = GuestSessionDTO.builder()
            .guestToken(guestSessionToken)
            .build();

        return ApiResponse.<GuestSessionDTO>builder()
            .message("OTP verified successfully")
            .data(response)
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

}