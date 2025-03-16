package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.service.interfaces.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ApiResponse<List<HotelResponseDTO>> getAllHotels() {
        return ApiResponse.<List<HotelResponseDTO>>builder()
                .statusCode(HttpStatus.OK)
                .message("Hotels retrieved successfully")
                .data(hotelService.getAllHotels())
                .build();
    }

    @PostMapping
    public ApiResponse<HotelResponseDTO> addHotel(@RequestBody HotelRequestDTO hotelRequestDTO) {
        return ApiResponse.<HotelResponseDTO>builder()
                .statusCode(HttpStatus.CREATED)
                .message("Hotel added successfully")
                .data(hotelService.addHotel(hotelRequestDTO))
                .build();
    }

}

