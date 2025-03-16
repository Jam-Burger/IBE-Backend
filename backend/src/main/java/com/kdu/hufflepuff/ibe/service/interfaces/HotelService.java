package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;

import java.util.List;
import java.util.UUID;

public interface HotelService {
    List<HotelResponseDTO> getAllHotels();
    HotelResponseDTO addHotel(HotelRequestDTO hotelRequestDTO);
}
