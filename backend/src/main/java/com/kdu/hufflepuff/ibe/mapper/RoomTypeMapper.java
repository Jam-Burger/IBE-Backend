package com.kdu.hufflepuff.ibe.mapper;

import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomTypeMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {
        modelMapper.createTypeMap(RoomType.class, RoomTypeDetailsDTO.class)
            .addMappings(mapper -> mapper.map(RoomType::getRoomTypeId, RoomTypeDetailsDTO::setRoomTypeId));

        modelMapper.createTypeMap(RoomTypeExtension.class, RoomTypeDetailsDTO.class)
            .addMappings(mapper -> mapper.map(RoomTypeExtension::getNoOfReviews, RoomTypeDetailsDTO::setNumberOfReviews));
    }

    public RoomTypeDetailsDTO toDto(RoomType roomType) {
        RoomTypeDetailsDTO dto = modelMapper.map(roomType, RoomTypeDetailsDTO.class);
        if (dto.getLandmark() == null && roomType.getProperty() != null) {
            dto.setLandmark(roomType.getProperty().getPropertyAddress());
        }
        return dto;
    }

    public RoomTypeDetailsDTO toDto(RoomType roomType, RoomTypeExtension extension) {
        RoomTypeDetailsDTO dto = toDto(roomType);
        log.info("Mapping room type extension to DTO: {}", dto);
        if (extension != null) {
            modelMapper.map(extension, dto);
        }
        dto.setPropertyId(roomType.getProperty().getPropertyId());
        dto.setLandmark(roomType.getProperty().getPropertyAddress());
        return dto;
    }
}