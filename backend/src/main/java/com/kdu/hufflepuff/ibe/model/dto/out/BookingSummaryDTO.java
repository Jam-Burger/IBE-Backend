package com.kdu.hufflepuff.ibe.model.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingSummaryDTO {
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int adultCount;
    private int childCount;
    private double totalCost;
    private String propertyName;
    private String propertyAddress;
    private String contactNumber;
    private String status;
    private String roomTypeImage;
}
