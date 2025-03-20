package com.kdu.hufflepuff.ibe.model.graphql;

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
public class Booking {
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer adultCount;
    private Integer childCount;
    private Integer totalCost;
    private Integer amountDueAtResort;
    private Property propertyBooked;
    private Long propertyId;
    private BookingStatus bookingStatus;
    private Long statusId;
    private Guest guest;
    private Long guestId;
    private Promotion promotionApplied;
    private Long promotionId;
    private List<RoomAvailability> roomBooked;
} 