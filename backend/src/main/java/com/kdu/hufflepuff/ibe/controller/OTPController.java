package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.service.interfaces.OTPService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{tenantId}/otp")
@Tag(name = "OTP", description = "One-Time Password generation and verification API")
public class OTPController {
    private final OTPService otpService;

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
            @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
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
    public ResponseEntity<String> verifyOtp(
            @Parameter(description = "ID of the tenant") @PathVariable Long tenantId,
            @Parameter(description = "Email address associated with the OTP", required = true) @RequestParam String email,
            @Parameter(description = "One-Time Password to verify", required = true) @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }
}