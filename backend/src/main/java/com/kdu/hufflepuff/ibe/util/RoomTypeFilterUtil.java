package com.kdu.hufflepuff.ibe.util;

import com.kdu.hufflepuff.ibe.model.dto.in.RoomTypeFilterDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomRateDetailsDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.RoomTypeDetailsDTO;
import com.kdu.hufflepuff.ibe.model.enums.SortOption;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Utility class to handle room type filtering and sorting operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoomTypeFilterUtil {
    public static List<RoomTypeDetailsDTO> filterAndSortRoomTypes(List<RoomTypeDetailsDTO> roomTypes,
                                                                  RoomTypeFilterDTO filter) {
        List<RoomTypeDetailsDTO> filteredRoomTypes = roomTypes.stream()
            .filter(roomType -> filter.getTotalGuests() == null ||
                roomType.getMaxCapacity() >= filter.getTotalGuests())
            .filter(roomType -> filter.getRoomSizeMin() == null ||
                roomType.getAreaInSquareFeet() >= filter.getRoomSizeMin())
            .filter(roomType -> filter.getRoomSizeMax() == null ||
                roomType.getAreaInSquareFeet() <= filter.getRoomSizeMax())
            .filter(roomType -> filterByBedTypes(roomType, filter.getBedTypes()))
            .filter(roomType -> filterByRatings(roomType, filter.getRatings()))
            .filter(roomType -> filterByAmenities(roomType, filter.getAmenities()))
            .filter(roomType -> filter.getIsAccessible() == null ||
                (roomType.getAmenities() != null &&
                    roomType.getAmenities().contains("Accessible")))
            .toList();

        return sortRoomTypes(filteredRoomTypes, filter);
    }

    private static boolean filterByBedTypes(RoomTypeDetailsDTO roomType, List<String> bedTypes) {
        if (bedTypes == null || bedTypes.isEmpty()) {
            return true;
        }
        return bedTypes.stream().anyMatch(bedType -> {
            if ("single".equalsIgnoreCase(bedType)) {
                return roomType.getSingleBed() > 0;
            } else if ("double".equalsIgnoreCase(bedType)) {
                return roomType.getDoubleBed() > 0;
            }
            return false;
        });
    }

    private static boolean filterByRatings(RoomTypeDetailsDTO roomType, List<Integer> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return true;
        }
        return ratings.stream().anyMatch(rating -> roomType.getRating() != null && roomType.getRating() >= rating);
    }

    private static boolean filterByAmenities(RoomTypeDetailsDTO roomType, List<String> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return true;
        }
        List<String> roomAmenities = roomType.getAmenities();
        return roomAmenities != null && new HashSet<>(roomAmenities).containsAll(amenities);
    }

    private static List<RoomTypeDetailsDTO> sortRoomTypes(List<RoomTypeDetailsDTO> roomTypes,
                                                          RoomTypeFilterDTO filter) {
        if (filter.getSortBy() == null) {
            return roomTypes;
        }

        List<RoomTypeDetailsDTO> sortedRoomTypes = new java.util.ArrayList<>(roomTypes);

        switch (filter.getSortBy()) {
            case RATING_HIGH_TO_LOW:
                sortedRoomTypes.sort(Comparator.comparing(RoomTypeDetailsDTO::getRating,
                    Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case CAPACITY_HIGH_TO_LOW:
                sortedRoomTypes.sort(Comparator.comparing(RoomTypeDetailsDTO::getMaxCapacity,
                    Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case ROOM_SIZE_LARGE_TO_SMALL:
                sortedRoomTypes.sort(Comparator.comparing(RoomTypeDetailsDTO::getAreaInSquareFeet,
                    Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case PRICE_LOW_TO_HIGH:
                sortedRoomTypes.sort(Comparator.comparing(
                    roomType -> getAveragePrice(roomType.getRoomRates()),
                    Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case PRICE_HIGH_TO_LOW:
                sortedRoomTypes.sort(Comparator.comparing(
                    roomType -> getAveragePrice(roomType.getRoomRates()),
                    Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            default:
                break;
        }

        return sortedRoomTypes;
    }

    private static Double getAveragePrice(List<RoomRateDetailsDTO> roomRates) {
        if (roomRates == null || roomRates.isEmpty()) {
            return null;
        }
        return roomRates.stream()
            .mapToDouble(RoomRateDetailsDTO::getPrice)
            .average()
            .orElse(0.0);
    }
}