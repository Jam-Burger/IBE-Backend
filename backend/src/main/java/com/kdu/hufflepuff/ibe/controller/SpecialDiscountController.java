package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.CreateSpecialDiscountRequest;
import com.kdu.hufflepuff.ibe.model.entity.SpecialDiscount;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/{tenantId}")
@RequiredArgsConstructor
public class SpecialDiscountController {
    private final SpecialDiscountService specialDiscountService;

    @GetMapping("{propertyId}/special-discounts")
    public ResponseEntity<ApiResponse<List<SpecialDiscount>>> getSpecialDiscounts(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SpecialDiscount> specialDiscounts = specialDiscountService.getSpecialDiscounts(tenantId, propertyId, startDate, endDate);
        return ApiResponse.<List<SpecialDiscount>>builder()
            .data(specialDiscounts)
            .message("Special discounts retrieved successfully")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }

    @PostMapping("special-discounts")
    public ResponseEntity<ApiResponse<SpecialDiscount>> createSpecialDiscount(
        @PathVariable Long tenantId,
        @Valid @RequestBody CreateSpecialDiscountRequest request
    ) {
        SpecialDiscount createdDiscount = specialDiscountService.createSpecialDiscount(tenantId, request);
        return ApiResponse.<SpecialDiscount>builder()
            .data(createdDiscount)
            .message("Special discount created successfully")
            .statusCode(HttpStatus.CREATED)
            .build()
            .send();
    }
}
