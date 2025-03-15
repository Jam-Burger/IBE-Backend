package com.kdu.hufflepuff.ibe.mapper;

import com.kdu.hufflepuff.ibe.model.dto.in.HotelRequestDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.HotelResponseDTO;
import com.kdu.hufflepuff.ibe.model.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface HotelMapper {
    HotelResponseDTO toDto(Hotel hotel);
    List<HotelResponseDTO> toDto(List<Hotel> hotels);
    Hotel toEntity(HotelRequestDTO hotelRequestDTO);
}

