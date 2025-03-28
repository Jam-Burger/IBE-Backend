package com.kdu.hufflepuff.ibe.mapper;

import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomTypeMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {
        modelMapper.createTypeMap(RoomType.class, RoomTypeDetailsDTO.class)
            .addMappings(mapper -> mapper.map(src -> src.getProperty().getPropertyId(), RoomTypeDetailsDTO::setPropertyId));

        modelMapper.createTypeMap(RoomTypeExtension.class, RoomTypeDetailsDTO.class)
            .addMappings(mapper -> mapper.map(RoomTypeExtension::getNoOfReviews, RoomTypeDetailsDTO::setNumberOfReviews));
    }

    public RoomTypeDetailsDTO toDto(RoomType roomType) {
        return modelMapper.map(roomType, RoomTypeDetailsDTO.class);
    }

    public void mapExtensionToDto(RoomTypeExtension extension, RoomTypeDetailsDTO dto) {
        modelMapper.map(extension, dto);
    }

    public RoomTypeDetailsDTO toDto(RoomType roomType, RoomTypeExtension extension) {
        RoomTypeDetailsDTO dto = toDto(roomType);
        if (extension != null) {
            mapExtensionToDto(extension, dto);
        }
        return dto;
    }
} 