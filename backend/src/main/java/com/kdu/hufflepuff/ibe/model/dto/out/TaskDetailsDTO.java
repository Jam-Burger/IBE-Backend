package com.kdu.hufflepuff.ibe.model.dto.out;

import com.kdu.hufflepuff.ibe.model.entity.CleanTaskType;
import com.kdu.hufflepuff.ibe.model.entity.Staff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailsDTO {
    private Staff staff;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer roomNumber;

    private LocalDate date;

    private CleanTaskType taskType;
}
