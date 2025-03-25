package com.kdu.hufflepuff.ibe.model.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatus {
    @JsonProperty("status_id")
    private Long statusId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("is_deactivated")
    private Boolean isDeactivated;

    private List<Booking> bookings;
} 