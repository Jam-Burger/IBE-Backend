package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.mapper.RoomTypeMapper;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import com.kdu.hufflepuff.ibe.repository.jpa.RoomTypeRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {
    private final GraphQlClient graphQlClient;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    public List<RoomTypeDetailsDTO> getRoomTypesByPropertyId(Long tenantId, Long propertyId) {
        List<RoomType> roomTypes = fetchRoomTypesByPropertyId(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Root types not found for property: " + propertyId));

        return roomTypes.stream()
            .map(this::convertToRoomTypeDetailsDTO)
            .toList();
    }

    private Optional<List<RoomType>> fetchRoomTypesByPropertyId(Long propertyId) {
        String query = """
                query getRoomTypes($propertyId: Int!) {
                    listRoomTypes(where: {
                        property_id: {equals: $propertyId}
                    }) {
                        room_type_id
                        room_type_name
                        max_capacity
                        area_in_square_feet
                        single_bed
                        double_bed
                        property_id
                    }
                }
            """;

        return Optional.ofNullable(graphQlClient.document(query)
            .variable("propertyId", propertyId)
            .retrieve("listRoomTypes")
            .toEntityList(RoomType.class)
            .block());
    }

    private RoomTypeDetailsDTO convertToRoomTypeDetailsDTO(RoomType roomType) {
        RoomTypeExtension extension = roomTypeRepository.findById(roomType.getRoomTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Room type extension not found: " + roomType.getRoomTypeId()));
        return roomTypeMapper.toDto(roomType, extension);
    }

    @Override
    @Transactional
    public void updateRoomTypeImages(Long tenantId, Long roomTypeId, List<String> imageUrls) {
        RoomTypeExtension extension = roomTypeRepository.findById(roomTypeId)
            .orElseThrow(() -> new ResourceNotFoundException("Room type extension not found: " + roomTypeId));
        extension.setImages(imageUrls);
        roomTypeRepository.save(extension);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAmenitiesByPropertyId(Long tenantId, Long propertyId) {
        List<RoomTypeDetailsDTO> roomTypes = getRoomTypesByPropertyId(tenantId, propertyId);
        return roomTypes.stream()
            .map(RoomTypeDetailsDTO::getAmenities)
            .flatMap(List::stream)
            .distinct()
            .toList();
    }
} 