package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.entity.SpecialOffer;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialDiscountService;
import jakarta.validation.Valid;
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
public class SpecialDiscountController {
    private final SpecialDiscountService specialDiscountService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialOffer>>> getSpecialDiscounts(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SpecialOffer> specialOffers = specialDiscountService.getSpecialDiscounts(tenantId, propertyId, startDate, endDate);
        return ApiResponse.<List<SpecialOffer>>builder()
            .data(specialOffers)
            .message("Special discounts retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
}
