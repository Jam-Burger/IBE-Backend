package com.kdu.hufflepuff.ibe.model.dto.in;

import com.kdu.hufflepuff.ibe.model.enums.SortOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeFilterDTO {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer roomCount;
    private Boolean isAccessible;
    private Integer totalGuests;
    private List<String> bedTypes;
    private List<Integer> ratings;
    private List<String> amenities;
    private Integer roomSizeMin;
    private Integer roomSizeMax;
    private SortOption sortBy;
} 