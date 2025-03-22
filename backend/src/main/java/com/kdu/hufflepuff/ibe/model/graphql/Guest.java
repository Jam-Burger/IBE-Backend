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
public class Guest {
    @JsonProperty("guest_id")
    private Long guestId;
    
    @JsonProperty("guest_name")
    private String guestName;
    
    private List<Booking> bookings;
} 