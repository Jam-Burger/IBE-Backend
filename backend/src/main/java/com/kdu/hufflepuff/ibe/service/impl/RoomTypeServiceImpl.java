package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ResourceNotFoundException;
import com.kdu.hufflepuff.ibe.mapper.RoomTypeMapper;
import com.kdu.hufflepuff.ibe.model.dto.in.RoomTypeFilterDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.entity.RoomTypeExtension;
import com.kdu.hufflepuff.ibe.model.graphql.RoomType;
import com.kdu.hufflepuff.ibe.repository.jpa.RoomTypeRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomTypeService;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import com.kdu.hufflepuff.ibe.util.RoomTypeFilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {
    private final GraphQlClient graphQlClient;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;
    private final RoomRateService roomRateService;

    @Override
    public List<RoomTypeDetailsDTO> getRoomTypesByPropertyId(Long tenantId, Long propertyId) {
        List<RoomType> roomTypes = fetchRoomTypesByPropertyId(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Root types not found for property: " + propertyId));

        return roomTypes.stream()
            .map(this::convertToRoomTypeDetailsDTO)
            .toList();
    }

    private Optional<List<RoomType>> fetchRoomTypesByPropertyId(Long propertyId) {
        return Optional.ofNullable(
            graphQlClient.document(GraphQLQueries.GET_ROOM_TYPES_BY_PROPERTY)
                .variable("propertyId", propertyId)
                .retrieve("listRoomTypes")
                .toEntityList(RoomType.class)
                .block()
        );
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
    public List<RoomTypeDetailsDTO> filterRoomTypes(Long tenantId, Long propertyId, RoomTypeFilterDTO filter) {
        List<RoomTypeDetailsDTO> allRoomTypes = getRoomTypesByPropertyId(tenantId, propertyId);

        Map<Long, Double> averagePrices = roomRateService.getAveragePricesByRoomType(
            propertyId, filter.getDateFrom(), filter.getDateTo());

        allRoomTypes.forEach(roomType -> {
            Double avgPrice = averagePrices.get(roomType.getRoomTypeId());
            roomType.setAveragePrice(avgPrice);
        });

        List<RoomTypeDetailsDTO> filteredRoomTypes = RoomTypeFilterUtil.filterAndSortRoomTypes(allRoomTypes, filter);
        log.info("Found {} room types after filtering", filteredRoomTypes.size());
        return filteredRoomTypes;
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