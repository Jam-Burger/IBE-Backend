package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/{tenantId}/{propertyId}/special-discounts")
@RequiredArgsConstructor
public class SpecialOfferController {

    private final SpecialOfferService specialOfferService;

    @Operation(
            summary = "Get calendar special offers",
            description = "Retrieve all special offers for a given date range for calendar view"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved offers",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SpecialOfferResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/calender-offers")
    public ResponseEntity<ApiResponse<List<SpecialOfferResponseDTO>>> getCalenderOffers(
            @Parameter(description = "Tenant ID", example = "1") @PathVariable Long tenantId,
            @Parameter(description = "Property ID", example = "101") @PathVariable Long propertyId,
            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2025-04-01")
            @Valid @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", example = "2025-04-30")
            @Valid @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SpecialOfferResponseDTO> specialOffers = specialOfferService.getCalenderOffers(tenantId, propertyId, startDate, endDate);
        return ApiResponse.<List<SpecialOfferResponseDTO>>builder()
                .data(specialOffers)
                .message("Special discounts retrieved successfully")
                .statusCode(HttpStatus.OK)
                .build()
                .send();
    }

    @Operation(
            summary = "Get all special offers",
            description = "Retrieve all special offers for a given property and date range"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved offers",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SpecialOfferResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialOfferResponseDTO>>> getSpecialOffers(
            @Parameter(description = "Tenant ID", example = "1") @PathVariable Long tenantId,
            @Parameter(description = "Property ID", example = "101") @PathVariable Long propertyId,
            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2025-04-01")
            @Valid @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", example = "2025-04-30")
            @Valid @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SpecialOfferResponseDTO> specialOffers = specialOfferService.getSpecialOffers(tenantId, propertyId, startDate, endDate);
        return ApiResponse.<List<SpecialOfferResponseDTO>>builder()
                .data(specialOffers)
                .message("Special discounts retrieved successfully")
                .statusCode(HttpStatus.OK)
                .build()
                .send();
    }

    @Operation(
            summary = "Get promo offer by code",
            description = "Retrieve a special promotional offer using a promo code and date range"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promo offer retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SpecialOfferResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid promo code or input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Promo offer not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/promo-offer")
    public ResponseEntity<ApiResponse<SpecialOfferResponseDTO>> getPromoOffer(
            @Parameter(description = "Tenant ID", example = "1") @PathVariable Long tenantId,
            @Parameter(description = "Property ID", example = "101") @PathVariable Long propertyId,
            @Parameter(description = "Promo Code", example = "SUMMER25")
            @Valid @NotNull @RequestParam("promo_code") String promoCode,
            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2025-04-01")
            @Valid @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", example = "2025-04-30")
            @Valid @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        SpecialOfferResponseDTO specialOffer = specialOfferService.getPromoOffer(tenantId, propertyId, promoCode, startDate, endDate);
        return ApiResponse.<SpecialOfferResponseDTO>builder()
                .data(specialOffer)
                .message("Promo offer retrieved successfully")
                .statusCode(HttpStatus.OK)
                .build()
                .send();
    }
}
