package com.kdu.hufflepuff.ibe.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyRoomRateDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Integer minimumRate;
} 