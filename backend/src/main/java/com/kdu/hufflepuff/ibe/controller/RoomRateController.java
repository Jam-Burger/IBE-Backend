package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
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
public class RoomRateController {
    private final RoomRateService roomRateService;

    @GetMapping("/minimum-daily")
    public ResponseEntity<ApiResponse<List<DailyRoomRateDTO>>> getMinimumDailyRates(
        @PathVariable Long tenantId,
        @PathVariable Long propertyId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DailyRoomRateDTO> minimumDailyRates = roomRateService.getMinimumDailyRates(tenantId, propertyId, startDate, endDate);
        return ApiResponse.<List<DailyRoomRateDTO>>builder()
            .data(minimumDailyRates)
            .message("Minimum daily room rates")
            .statusCode(HttpStatus.OK)
            .build().send();
    }
} 