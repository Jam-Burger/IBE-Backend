package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long promotionId = 1L;
        Double priceFactor = 0.85;
        String promotionTitle = "Summer Sale";
        String promotionDescription = "Special summer discount";
        Boolean isDeactivated = false;
        Integer minimumDaysOfStay = 3;
        List<Booking> bookings = Collections.singletonList(
            Booking.builder()
                .bookingId(1L)
                .build()
        );

        // When
        Promotion promotion = Promotion.builder()
            .promotionId(promotionId)
            .priceFactor(priceFactor)
            .promotionTitle(promotionTitle)
            .promotionDescription(promotionDescription)
            .isDeactivated(isDeactivated)
            .minimumDaysOfStay(minimumDaysOfStay)
            .bookings(bookings)
            .build();

        // Then
        assertThat(promotion)
            .isNotNull()
            .satisfies(p -> {
                assertThat(p.getPromotionId()).isEqualTo(promotionId);
                assertThat(p.getPriceFactor()).isEqualTo(priceFactor);
                assertThat(p.getPromotionTitle()).isEqualTo(promotionTitle);
                assertThat(p.getPromotionDescription()).isEqualTo(promotionDescription);
                assertThat(p.getIsDeactivated()).isEqualTo(isDeactivated);
                assertThat(p.getMinimumDaysOfStay()).isEqualTo(minimumDaysOfStay);
                assertThat(p.getBookings())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(bookings);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Promotion promotion = new Promotion();

        // Then
        assertThat(promotion)
            .isNotNull()
            .satisfies(p -> {
                assertThat(p.getPromotionId()).isNull();
                assertThat(p.getPriceFactor()).isNull();
                assertThat(p.getPromotionTitle()).isNull();
                assertThat(p.getPromotionDescription()).isNull();
                assertThat(p.getIsDeactivated()).isNull();
                assertThat(p.getMinimumDaysOfStay()).isNull();
                assertThat(p.getBookings()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Promotion promotion1 = Promotion.builder()
            .promotionId(1L)
            .priceFactor(0.85)
            .promotionTitle("Summer Sale")
            .promotionDescription("Special summer discount")
            .isDeactivated(false)
            .minimumDaysOfStay(3)
            .build();

        Promotion promotion2 = Promotion.builder()
            .promotionId(1L)
            .priceFactor(0.85)
            .promotionTitle("Summer Sale")
            .promotionDescription("Special summer discount")
            .isDeactivated(false)
            .minimumDaysOfStay(3)
            .build();

        Promotion promotion3 = Promotion.builder()
            .promotionId(2L)
            .priceFactor(0.75)
            .promotionTitle("Winter Sale")
            .promotionDescription("Special winter discount")
            .isDeactivated(true)
            .minimumDaysOfStay(5)
            .build();

        // Then
        assertThat(promotion1)
            .isEqualTo(promotion2)    // Equal objects
            .isNotEqualTo(promotion3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(promotion1.hashCode())
            .isEqualTo(promotion2.hashCode())
            .isNotEqualTo(promotion3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Promotion promotion = Promotion.builder()
            .promotionId(1L)
            .priceFactor(0.85)
            .promotionTitle("Summer Sale")
            .promotionDescription("Special summer discount")
            .isDeactivated(false)
            .minimumDaysOfStay(3)
            .build();

        // Then
        assertThat(promotion.toString())
            .isNotNull()
            .contains(
                "promotionId=1",
                "priceFactor=0.85",
                "promotionTitle=Summer Sale",
                "promotionDescription=Special summer discount",
                "isDeactivated=false",
                "minimumDaysOfStay=3"
            );
    }

    @Test
    void testSetters() {
        // Given
        Promotion promotion = new Promotion();
        Long promotionId = 1L;
        Double priceFactor = 0.85;
        String promotionTitle = "Summer Sale";
        String promotionDescription = "Special summer discount";
        Boolean isDeactivated = false;
        Integer minimumDaysOfStay = 3;
        List<Booking> bookings = Collections.singletonList(
            Booking.builder()
                .bookingId(1L)
                .build()
        );

        // When
        promotion.setPromotionId(promotionId);
        promotion.setPriceFactor(priceFactor);
        promotion.setPromotionTitle(promotionTitle);
        promotion.setPromotionDescription(promotionDescription);
        promotion.setIsDeactivated(isDeactivated);
        promotion.setMinimumDaysOfStay(minimumDaysOfStay);
        promotion.setBookings(bookings);

        // Then
        assertThat(promotion)
            .satisfies(p -> {
                assertThat(p.getPromotionId()).isEqualTo(promotionId);
                assertThat(p.getPriceFactor()).isEqualTo(priceFactor);
                assertThat(p.getPromotionTitle()).isEqualTo(promotionTitle);
                assertThat(p.getPromotionDescription()).isEqualTo(promotionDescription);
                assertThat(p.getIsDeactivated()).isEqualTo(isDeactivated);
                assertThat(p.getMinimumDaysOfStay()).isEqualTo(minimumDaysOfStay);
                assertThat(p.getBookings()).isEqualTo(bookings);
            });
    }
} 