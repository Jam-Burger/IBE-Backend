package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;

import java.util.List;

public interface HotelService {
    List<HotelResponseDTO> getAllHotels();

    HotelResponseDTO addHotel(HotelRequestDTO hotelRequestDTO);
}
