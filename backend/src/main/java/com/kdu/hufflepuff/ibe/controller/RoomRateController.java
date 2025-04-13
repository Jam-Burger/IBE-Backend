package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}/{propertyId}/room-rates")
@RequiredArgsConstructor
@Tag(name = "Room Rates", description = "APIs for managing room rates")
public class RoomRateController {
    private final RoomRateService roomRateService;

    @Operation(
        summary = "Get minimum daily room rates",
        description = "Retrieves the minimum daily room rates for a given property within a date range"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved minimum daily room rates",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.kdu.hufflepuff.ibe.model.response.ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date range or parameters provided"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Property or tenant not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/daily-minimum")
    public ResponseEntity<com.kdu.hufflepuff.ibe.model.response.ApiResponse<List<DailyRoomRateDTO>>> getMinimumDailyRates(
        @Parameter(description = "Tenant ID", required = true)
        @PathVariable Long tenantId,

        @Parameter(description = "Property ID", required = true)
        @PathVariable Long propertyId,

        @Parameter(description = "Start date in ISO format (yyyy-MM-dd)", required = true, example = "2025-01-01")
        @Valid @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

        @Parameter(description = "End date in ISO format (yyyy-MM-dd)", required = true, example = "2025-01-10")
        @Valid @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DailyRoomRateDTO> minimumDailyRates = roomRateService.getMinimumDailyRates(tenantId, propertyId, startDate,
            endDate);
        return com.kdu.hufflepuff.ibe.model.response.ApiResponse.<List<DailyRoomRateDTO>>builder()
            .data(minimumDailyRates)
            .message("Minimum daily room rates")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
}