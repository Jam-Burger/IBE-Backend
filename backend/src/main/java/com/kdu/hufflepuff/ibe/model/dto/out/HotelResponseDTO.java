package com.kdu.hufflepuff.ibe.model.dto.out;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponseDTO {
    private UUID id;
    private String name;
    private String address;
    private int starRating;
    private BigDecimal pricePerNight;
    private String description;
    private Instant createdAt;
}

