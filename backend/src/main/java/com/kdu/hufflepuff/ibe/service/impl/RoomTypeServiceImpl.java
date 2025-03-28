package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.mapper.RoomTypeMapper;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import com.kdu.hufflepuff.ibe.repository.jpa.RoomTypeExtensionRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {
    private final GraphQlClient graphQlClient;
    private final RoomTypeExtensionRepository roomTypeExtensionRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeDetailsDTO> getRoomTypesByPropertyId(Long tenantId, Long propertyId) {
        List<RoomType> roomTypes = fetchRoomTypesByPropertyId(propertyId);
        if (roomTypes == null) {
            return List.of();
        }
        return roomTypes.stream()
            .map(this::convertToRoomTypeDetailsDTO)
            .toList();
    }

    private List<RoomType> fetchRoomTypesByPropertyId(Long propertyId) {
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

        return graphQlClient.document(query)
            .variable("propertyId", propertyId)
            .retrieve("listRoomTypes")
            .toEntityList(RoomType.class)
            .block();
    }

    private RoomTypeDetailsDTO convertToRoomTypeDetailsDTO(RoomType roomType) {
        RoomTypeExtension extension = roomTypeExtensionRepository.findById(roomType.getRoomTypeId()).orElse(null);
        return roomTypeMapper.toDto(roomType, extension);
    }
} 