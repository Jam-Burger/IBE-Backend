package com.kdu.hufflepuff.ibe.service;

import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import com.kdu.hufflepuff.ibe.model.dto.out.DailyRoomRateDTO;
import com.kdu.hufflepuff.ibe.model.dto.out.SpecialOfferResponseDTO;
import com.kdu.hufflepuff.ibe.model.graphql.*;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import com.kdu.hufflepuff.ibe.service.interfaces.SpecialOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = IbeApplication.class)
public class RoomRateAndSpecialOfferIntegrationTest {

    private static final Long TENANT_ID = 1L;
    private static final Long PROPERTY_ID = 100L;
    private static final Long ROOM_TYPE_ID_1 = 200L;
    private static final Long ROOM_TYPE_ID_2 = 201L;
    private static final Long ROOM_ID_1 = 300L;
    private static final Long ROOM_ID_2 = 301L;

    @Autowired
    private RoomRateService roomRateService;

    @MockBean
    private SpecialOfferService specialOfferService;

    @MockBean
    private GraphQlClient graphQlClient;

    @MockBean
    private GraphQlClient.RequestSpec requestSpec;

    @MockBean
    private GraphQlClient.RetrieveSpec retrieveSpec;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        when(graphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.variable(anyString(), any())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);

        startDate = LocalDate.now();
        endDate = startDate.plusDays(3); // 4-day stay
    }

    @Test
    void getMinimumDailyRates_WithMultipleOffers_ShouldSelectBestDiscount() {
        // Arrange
        // Create two rooms with different rates
        Room room1 = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1,
            new double[]{100.0, 110.0, 120.0, 130.0});  // Daily rates for room type 1
        Room room2 = createRoom(ROOM_ID_2, ROOM_TYPE_ID_2,
            new double[]{90.0, 100.0, 110.0, 120.0});   // Daily rates for room type 2

        List<RoomAvailability> availabilities = Arrays.asList(
            createRoomAvailability(room1),
            createRoomAvailability(room2)
        );

        // Create multiple special offers with different discount percentages
        List<SpecialOfferResponseDTO> specialOffers = Arrays.asList(
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(10.0)  // 10% discount
                .startDate(startDate)
                .endDate(endDate)
                .title("Standard Discount")
                .build(),
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(20.0)  // 20% discount
                .startDate(startDate)
                .endDate(startDate.plusDays(1)) // Only valid for first two days
                .title("Limited Time Offer")
                .build(),
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(25.0)  // 25% discount
                .startDate(startDate.plusDays(2))
                .endDate(endDate)  // Only valid for last two days
                .title("Weekend Special")
                .build()
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Arrays.asList(room1, room2)));
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, startDate, endDate))
            .thenReturn(specialOffers);

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(4);

        // Verify service integration
        verify(specialOfferService).getCalenderOffers(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Day 1: Room Type 2 is cheapest (90) with 20% discount = 72
        DailyRoomRateDTO day1 = result.get(0);
        assertThat(day1.getDate()).isEqualTo(startDate);
        assertThat(day1.getMinimumRate()).isEqualTo(90.0);  // Room type 2
        assertThat(day1.getDiscountedRate()).isEqualTo(72.0);  // 90 - 20%

        // Day 2: Room Type 2 is cheapest (100) with 20% discount = 80
        DailyRoomRateDTO day2 = result.get(1);
        assertThat(day2.getDate()).isEqualTo(startDate.plusDays(1));
        assertThat(day2.getMinimumRate()).isEqualTo(100.0);  // Room type 2
        assertThat(day2.getDiscountedRate()).isEqualTo(80.0);  // 100 - 20%

        // Day 3: Room Type 2 is cheapest (110) with 25% discount = 82.5
        DailyRoomRateDTO day3 = result.get(2);
        assertThat(day3.getDate()).isEqualTo(startDate.plusDays(2));
        assertThat(day3.getMinimumRate()).isEqualTo(110.0);  // Room type 2
        assertThat(day3.getDiscountedRate()).isEqualTo(82.5);  // 110 - 25%

        // Day 4: Room Type 2 is cheapest (120) with 25% discount = 90
        DailyRoomRateDTO day4 = result.get(3);
        assertThat(day4.getDate()).isEqualTo(startDate.plusDays(3));
        assertThat(day4.getMinimumRate()).isEqualTo(120.0);  // Room type 2
        assertThat(day4.getDiscountedRate()).isEqualTo(90.0);  // 120 - 25%
    }

    @Test
    void getMinimumDailyRates_WithOverlappingDateRanges_ShouldApplyBestDiscount() {
        // Arrange
        Room room = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1, new double[]{100.0, 100.0, 100.0, 100.0});
        List<RoomAvailability> availabilities = Collections.singletonList(createRoomAvailability(room));

        // Create offers with overlapping date ranges but different discounts
        List<SpecialOfferResponseDTO> specialOffers = Arrays.asList(
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(10.0)
                .startDate(startDate)
                .endDate(endDate)
                .title("Base Discount")
                .build(),
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(15.0)
                .startDate(startDate.plusDays(1))
                .endDate(endDate.minusDays(1))
                .title("Middle Stay Discount")
                .build(),
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(20.0)
                .startDate(startDate.plusDays(2))
                .endDate(endDate)
                .title("End Stay Discount")
                .build()
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Collections.singletonList(room)));
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, startDate, endDate))
            .thenReturn(specialOffers);

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(4);

        // Day 1: Only the 10% discount applies
        assertThat(result.get(0).getDiscountedRate()).isEqualTo(90.0);  // 100 - 10%

        // Day 2: Both 10% and 15% discounts apply, should use 15%
        assertThat(result.get(1).getDiscountedRate()).isEqualTo(85.0);  // 100 - 15%

        // Day 3: All three discounts apply, should use 20%
        assertThat(result.get(2).getDiscountedRate()).isEqualTo(80.0);  // 100 - 20%

        // Day 4: Both 10% and 20% discounts apply, should use 20%
        assertThat(result.get(3).getDiscountedRate()).isEqualTo(80.0);  // 100 - 20%
    }

    @Test
    void getMinimumDailyRates_WithDifferentRoomTypes_ShouldSelectCheapestRate() {
        // Arrange
        // Create two rooms with alternating cheapest rates
        Room room1 = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1,
            new double[]{80.0, 120.0, 80.0, 120.0});  // Room type 1 is cheaper on days 1 and 3
        Room room2 = createRoom(ROOM_ID_2, ROOM_TYPE_ID_2,
            new double[]{120.0, 80.0, 120.0, 80.0});  // Room type 2 is cheaper on days 2 and 4

        List<RoomAvailability> availabilities = Arrays.asList(
            createRoomAvailability(room1),
            createRoomAvailability(room2)
        );

        // Single discount that applies to all days
        List<SpecialOfferResponseDTO> specialOffers = Collections.singletonList(
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(25.0)
                .startDate(startDate)
                .endDate(endDate)
                .title("Full Stay Discount")
                .build()
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Arrays.asList(room1, room2)));
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, startDate, endDate))
            .thenReturn(specialOffers);

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(4);

        // Minimum rates should alternate between 80 for each day
        assertThat(result.get(0).getMinimumRate()).isEqualTo(80.0);  // Day 1 from Room 1
        assertThat(result.get(1).getMinimumRate()).isEqualTo(80.0);  // Day 2 from Room 2
        assertThat(result.get(2).getMinimumRate()).isEqualTo(80.0);  // Day 3 from Room 1
        assertThat(result.get(3).getMinimumRate()).isEqualTo(80.0);  // Day 4 from Room 2

        // All discounted rates should be 60 (80 - 25%)
        for (DailyRoomRateDTO daily : result) {
            assertThat(daily.getDiscountedRate()).isEqualTo(60.0);
        }
    }

    @Test
    void getMinimumDailyRates_WithNoSpecialOffers_ShouldReturnRatesWithoutDiscounts() {
        // Arrange
        Room room = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1, new double[]{100.0, 110.0, 120.0, 130.0});
        List<RoomAvailability> availabilities = Collections.singletonList(createRoomAvailability(room));

        // Empty special offers list
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, startDate, endDate))
            .thenReturn(Collections.emptyList());

        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Collections.singletonList(room)));

        // Act
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(4);

        // Minimum rates and discounted rates should be the same without discounts
        for (int i = 0; i < result.size(); i++) {
            DailyRoomRateDTO day = result.get(i);
            double expectedRate = 100.0 + (i * 10.0);
            assertThat(day.getMinimumRate()).isEqualTo(expectedRate);
            assertThat(day.getDiscountedRate()).isEqualTo(expectedRate);  // No discount
        }
    }

    @Test
    void getMinimumDailyRates_WithRatesOutsideDateRange_ShouldOnlyIncludeRelevantDates() {
        // Arrange
        // Create a room with rates that extend beyond the requested date range
        Room room = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1,
            new double[]{90.0, 100.0, 110.0, 120.0, 130.0, 140.0});  // Six days of rates

        List<RoomAvailability> availabilities = Collections.singletonList(createRoomAvailability(room));

        // Discount applies to all dates
        List<SpecialOfferResponseDTO> specialOffers = Collections.singletonList(
            SpecialOfferResponseDTO.builder()
                .propertyId(PROPERTY_ID)
                .discountPercentage(10.0)
                .startDate(startDate.minusDays(1))  // Starts before requested range
                .endDate(endDate.plusDays(2))       // Ends after requested range
                .title("Extended Discount")
                .build()
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(Room.class)).thenReturn(Mono.just(Collections.singletonList(room)));
        when(specialOfferService.getCalenderOffers(TENANT_ID, PROPERTY_ID, startDate, endDate))
            .thenReturn(specialOffers);

        // Act - Only request 4 days
        List<DailyRoomRateDTO> result = roomRateService.getMinimumDailyRates(TENANT_ID, PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(4);  // Should only return the 4 days that were requested

        // Check rates and discounts for each day in the range
        for (int i = 0; i < result.size(); i++) {
            DailyRoomRateDTO day = result.get(i);
            double expectedRate = 90.0 + (i * 10.0);
            double expectedDiscountedRate = expectedRate * 0.9;  // 10% discount

            assertThat(day.getMinimumRate()).isEqualTo(expectedRate);
            assertThat(day.getDiscountedRate()).isEqualTo(expectedDiscountedRate);
        }
    }

    // Helper methods
    private Room createRoom(Long roomId, Long roomTypeId, double[] rates) {
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomTypeId(roomTypeId);

        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(roomTypeId);

        List<RoomRateRoomTypeMapping> roomRateMappings = new ArrayList<>();

        // Create a rate for each day
        for (int i = 0; i < rates.length; i++) {
            RoomRate rate = new RoomRate();
            rate.setDate(startDate.plusDays(i));
            rate.setBasicNightlyRate(rates[i]);

            RoomRateRoomTypeMapping mapping = new RoomRateRoomTypeMapping();
            mapping.setRoomType(roomType);
            mapping.setRoomRate(rate);

            roomRateMappings.add(mapping);
        }

        roomType.setRoomRates(roomRateMappings);
        room.setRoomType(roomType);

        return room;
    }

    private RoomAvailability createRoomAvailability(Room room) {
        RoomAvailability availability = new RoomAvailability();
        availability.setRoom(room);
        return availability;
    }
} 