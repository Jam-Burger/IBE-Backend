package com.kdu.hufflepuff.ibe.model.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import com.kdu.hufflepuff.ibe.model.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO that combines information from both GraphQL Booking and JPA BookingExtension
 * to provide a comprehensive view of booking details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsDTO {
    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("check_in_date")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
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

    @JsonProperty("booking_status")
    private String bookingStatus;

    @JsonProperty("transaction")
    private Transaction transaction;

    @JsonProperty("property_id")
    private Long propertyId;

    @JsonProperty("room_numbers")
    private List<Integer> roomNumbers;

    @JsonProperty("special_offer")
    private SpecialOfferResponseDTO specialOffer;

    @JsonProperty("guest_details")
    private GuestExtension guestDetails;

    @JsonProperty("room_type_id")
    private Long roomTypeId;
} 