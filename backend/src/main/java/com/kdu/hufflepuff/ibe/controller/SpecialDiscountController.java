package com.kdu.hufflepuff.ibe.controller;

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
@RequestMapping("/api/v1/{tenantId}/{propertyId}/special-discounts")
@RequiredArgsConstructor
public class SpecialDiscountController {
    private final SpecialDiscountService specialDiscountService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialDiscount>>> getSpecialDiscounts(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<SpecialDiscount> specialDiscounts = specialDiscountService.getDiscounts(tenantId, propertyId, startDate, endDate);
        return ApiResponse.<List<SpecialDiscount>>builder()
            .data(specialDiscounts)
            .message("Special discounts")
            .statusCode(HttpStatus.OK)
            .build()
            .send();
    }
}
