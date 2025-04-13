package com.kdu.hufflepuff.ibe.model.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonProperty("check_in_date")
    private LocalDate checkInDate;

    @JsonProperty("check_out_date")
    private LocalDate checkOutDate;

    @JsonProperty("adult_count")
    private Integer adultCount;

    @JsonProperty("child_count")
    private Integer childCount;

    @JsonProperty("total_cost")
    private Integer totalCost;

    @JsonProperty("amount_due_at_resort")
    private Integer amountDueAtResort;

    @JsonProperty("promotion_id")
    private Long promotionId;

    @JsonProperty("property_booked")
    private Property propertyBooked;

    @JsonProperty("booking_status")
    private BookingStatus bookingStatus;

    private Guest guest;

    @JsonProperty("promotion_applied")
    private Promotion promotionApplied;

    @JsonProperty("room_booked")
    private List<RoomAvailability> roomBooked;
} 