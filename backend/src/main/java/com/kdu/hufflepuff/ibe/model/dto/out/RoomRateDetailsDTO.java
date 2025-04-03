package com.kdu.hufflepuff.ibe.model.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RoomRateDetailsDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Long roomTypeId;
    private Double price;
} 