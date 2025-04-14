package com.kdu.hufflepuff.ibe.model.dto.in;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAvailabilityDto {
    private Long staffId;
    private LocalDate date;
    private Boolean isAvailable;
}
