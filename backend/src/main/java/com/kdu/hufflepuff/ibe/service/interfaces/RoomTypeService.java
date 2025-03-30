package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.model.dto.in.RoomTypeFilterDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;

import java.util.List;

public interface RoomTypeService {
    List<RoomTypeDetailsDTO> getRoomTypesByPropertyId(Long tenantId, Long propertyId);

    /**
     * Get all unique amenities available across all room types for a property
     *
     * @param tenantId   The tenant ID
     * @param propertyId The property ID
     * @return A list of unique amenities
     */
    List<String> getAmenitiesByPropertyId(Long tenantId, Long propertyId);

    /**
     * Update room type images URLs
     *
     * @param tenantId   The tenant ID
     * @param roomTypeId The room type ID
     * @param imageUrls  List of image URLs to store
     */
    void updateRoomTypeImages(Long tenantId, Long roomTypeId, List<String> imageUrls);

    /**
     * Filter room types based on various criteria
     *
     * @param tenantId   The tenant ID
     * @param propertyId The property ID
     * @param filter     The filter criteria
     * @return Filtered list of room types
     */
    List<RoomTypeDetailsDTO> filterRoomTypes(
        Long tenantId,
        Long propertyId,
        RoomTypeFilterDTO filter
    );
} 