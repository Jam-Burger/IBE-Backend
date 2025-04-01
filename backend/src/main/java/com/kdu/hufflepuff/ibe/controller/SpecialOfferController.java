package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialOfferResponseDTO>>> getSpecialDiscounts(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @Valid @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
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

    @GetMapping("/promo-offer")
    public ResponseEntity<ApiResponse<SpecialOfferResponseDTO>> getPromoOffer(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @Valid @NotNull @RequestParam("promo_code") String promoCode,
        @Valid @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
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
