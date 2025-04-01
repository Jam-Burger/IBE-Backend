package com.kdu.hufflepuff.ibe.service;

import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import com.kdu.hufflepuff.ibe.model.graphql.*;
import com.kdu.hufflepuff.ibe.service.interfaces.RoomRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = IbeApplication.class)
public class RoomRateAveragePriceIntegrationTest {

    private static final Long PROPERTY_ID = 100L;
    private static final Long ROOM_TYPE_ID_1 = 200L;
    private static final Long ROOM_TYPE_ID_2 = 201L;
    private static final Long ROOM_ID_1 = 300L;
    private static final Long ROOM_ID_2 = 301L;
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Autowired
    private RoomRateService roomRateService;

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
    void getAveragePricesByRoomType_ReturnsCorrectAverages() {
        // Arrange
        // Create room availabilities
        Room room1 = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1);
        Room room2 = createRoom(ROOM_ID_2, ROOM_TYPE_ID_2);

        List<RoomAvailability> availabilities = Arrays.asList(
            createRoomAvailability(room1),
            createRoomAvailability(room2)
        );

        // Create room rates mappings for room type 1
        RoomType roomType1 = new RoomType();
        roomType1.setRoomTypeId(ROOM_TYPE_ID_1);

        RoomRate rate1Day1 = new RoomRate();
        rate1Day1.setDate(startDate);
        rate1Day1.setBasicNightlyRate(100.0);

        RoomRate rate1Day2 = new RoomRate();
        rate1Day2.setDate(startDate.plusDays(1));
        rate1Day2.setBasicNightlyRate(120.0);

        RoomRate rate1Day3 = new RoomRate();
        rate1Day3.setDate(startDate.plusDays(2));
        rate1Day3.setBasicNightlyRate(140.0);

        RoomRate rate1Day4 = new RoomRate();
        rate1Day4.setDate(startDate.plusDays(3));
        rate1Day4.setBasicNightlyRate(160.0);

        // Create room rates mappings for room type 2
        RoomType roomType2 = new RoomType();
        roomType2.setRoomTypeId(ROOM_TYPE_ID_2);

        RoomRate rate2Day1 = new RoomRate();
        rate2Day1.setDate(startDate);
        rate2Day1.setBasicNightlyRate(150.0);

        RoomRate rate2Day2 = new RoomRate();
        rate2Day2.setDate(startDate.plusDays(1));
        rate2Day2.setBasicNightlyRate(170.0);

        RoomRate rate2Day3 = new RoomRate();
        rate2Day3.setDate(startDate.plusDays(2));
        rate2Day3.setBasicNightlyRate(190.0);

        RoomRate rate2Day4 = new RoomRate();
        rate2Day4.setDate(startDate.plusDays(3));
        rate2Day4.setBasicNightlyRate(210.0);

        // Create mappings
        List<RoomRateRoomTypeMapping> mappings = Arrays.asList(
            createMapping(roomType1, rate1Day1),
            createMapping(roomType1, rate1Day2),
            createMapping(roomType1, rate1Day3),
            createMapping(roomType1, rate1Day4),
            createMapping(roomType2, rate2Day1),
            createMapping(roomType2, rate2Day2),
            createMapping(roomType2, rate2Day3),
            createMapping(roomType2, rate2Day4)
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(RoomRateRoomTypeMapping.class)).thenReturn(Mono.just(mappings));

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(2);

        // Room type 1 average: (100 + 120 + 140 + 160) / 4 = 130
        assertThat(result.get(ROOM_TYPE_ID_1)).isEqualTo(130.0);

        // Room type 2 average: (150 + 170 + 190 + 210) / 4 = 180
        assertThat(result.get(ROOM_TYPE_ID_2)).isEqualTo(180.0);

        // Verify GraphQL queries were made
        verify(graphQlClient, times(2)).document(anyString());
    }

    @Test
    void getAveragePricesByRoomType_HandlesDefaultDates_WhenNotProvided() {
        // Arrange
        // Create room availabilities
        Room room = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1);
        List<RoomAvailability> availabilities = Collections.singletonList(createRoomAvailability(room));

        // Create mappings for today and tomorrow only
        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(ROOM_TYPE_ID_1);

        RoomRate rateDay1 = new RoomRate();
        rateDay1.setDate(LocalDate.now());
        rateDay1.setBasicNightlyRate(100.0);

        RoomRate rateDay2 = new RoomRate();
        rateDay2.setDate(LocalDate.now().plusDays(1));
        rateDay2.setBasicNightlyRate(150.0);

        List<RoomRateRoomTypeMapping> mappings = Arrays.asList(
            createMapping(roomType, rateDay1),
            createMapping(roomType, rateDay2)
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(RoomRateRoomTypeMapping.class)).thenReturn(Mono.just(mappings));

        // Act - pass null dates to trigger default date behavior
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, null, null);

        // Assert
        assertThat(result).hasSize(1);

        // Room type 1 average: (100 + 150) / 2 = 125
        assertThat(result.get(ROOM_TYPE_ID_1)).isEqualTo(125.0);

        // Verify that appropriate variables were used in the query
        verify(requestSpec, atLeastOnce()).variable(eq("startDate"), contains(LocalDate.now().toString()));
        verify(requestSpec, atLeastOnce()).variable(eq("endDate"), contains(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    void getAveragePricesByRoomType_ReturnsEmptyMap_WhenNoAvailabilities() {
        // Arrange
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(Collections.emptyList()));

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).isEmpty();

        // Should not query for rates if no rooms are available
        verify(graphQlClient, never()).document(contains("GET_ROOM_RATE_MAPPINGS_BY_ROOM_TYPES"));
    }

    @Test
    void getAveragePricesByRoomType_HandlesNullRoomTypeIdOrRate() {
        // Arrange
        // Create room availabilities
        Room room = createRoom(ROOM_ID_1, ROOM_TYPE_ID_1);
        List<RoomAvailability> availabilities = Collections.singletonList(createRoomAvailability(room));

        // Create room type without ID (invalid case)
        RoomType nullIdRoomType = new RoomType();
        // ID intentionally not set

        // Create room type with regular ID
        RoomType validRoomType = new RoomType();
        validRoomType.setRoomTypeId(ROOM_TYPE_ID_1);

        // Create rates
        RoomRate nullRate = new RoomRate();
        nullRate.setDate(startDate);
        // Rate intentionally not set

        RoomRate validRate = new RoomRate();
        validRate.setDate(startDate);
        validRate.setBasicNightlyRate(100.0);

        List<RoomRateRoomTypeMapping> mappings = Arrays.asList(
            createMapping(nullIdRoomType, validRate),  // Invalid room type
            createMapping(validRoomType, nullRate),    // Invalid rate
            createMapping(validRoomType, validRate)    // Valid mapping
        );

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));
        when(retrieveSpec.toEntityList(RoomRateRoomTypeMapping.class)).thenReturn(Mono.just(mappings));

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).hasSize(1);

        // Should only include the valid rate from the valid room type
        assertThat(result.get(ROOM_TYPE_ID_1)).isEqualTo(100.0);
    }

    @Test
    void getAveragePricesByRoomType_ReturnsEmptyMap_WhenNoRoomTypeIds() {
        // Arrange
        // Create room with null room type ID
        Room room = new Room();
        room.setRoomId(ROOM_ID_1);
        // Room type ID intentionally not set

        RoomAvailability availability = createRoomAvailability(room);
        List<RoomAvailability> availabilities = Collections.singletonList(availability);

        // Mock responses
        when(retrieveSpec.toEntityList(RoomAvailability.class)).thenReturn(Mono.just(availabilities));

        // Explicitly mock the second query to return empty list
        // This prevents the NullPointerException when the method is called
        Mono<List<RoomRateRoomTypeMapping>> emptyMonoList = Mono.just(Collections.emptyList());
        when(retrieveSpec.toEntityList(eq(RoomRateRoomTypeMapping.class))).thenReturn(emptyMonoList);

        // Act
        Map<Long, Double> result = roomRateService.getAveragePricesByRoomType(PROPERTY_ID, startDate, endDate);

        // Assert
        assertThat(result).isEmpty();
    }

    // Helper methods
    private Room createRoom(Long roomId, Long roomTypeId) {
        Room room = new Room();
        room.setRoomId(roomId);
        room.setRoomTypeId(roomTypeId);
        return room;
    }

    private RoomAvailability createRoomAvailability(Room room) {
        RoomAvailability availability = new RoomAvailability();
        availability.setRoom(room);
        return availability;
    }

    private RoomRateRoomTypeMapping createMapping(RoomType roomType, RoomRate roomRate) {
        RoomRateRoomTypeMapping mapping = new RoomRateRoomTypeMapping();
        mapping.setRoomType(roomType);
        mapping.setRoomRate(roomRate);
        return mapping;
    }
} 