package com.kdu.hufflepuff.ibe.model.graphql;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyTest {

    @Test
    void testBuilderAndGetters() {
        // Given
        Long propertyId = 1L;
        String propertyName = "Grand Hotel";
        String propertyAddress = "123 Main St";
        String contactNumber = "+1234567890";
        Long tenantId = 1L;
        Tenant tenant = Tenant.builder().tenantId(tenantId).build();
        List<Room> rooms = Collections.singletonList(Room.builder().roomId(1L).build());
        List<RoomType> roomTypes = Collections.singletonList(RoomType.builder().roomTypeId(1L).build());
        List<RoomAvailability> roomAvailabilities = Collections.singletonList(RoomAvailability.builder().availabilityId(1L).build());
        List<Booking> bookings = Collections.singletonList(Booking.builder().bookingId(1L).build());

        // When
        Property property = Property.builder()
            .propertyId(propertyId)
            .propertyName(propertyName)
            .propertyAddress(propertyAddress)
            .contactNumber(contactNumber)
            .tenantId(tenantId)
            .tenant(tenant)
            .rooms(rooms)
            .roomTypes(roomTypes)
            .roomAvailabilities(roomAvailabilities)
            .bookings(bookings)
            .build();

        // Then
        assertThat(property)
            .isNotNull()
            .satisfies(p -> {
                assertThat(p.getPropertyId()).isEqualTo(propertyId);
                assertThat(p.getPropertyName()).isEqualTo(propertyName);
                assertThat(p.getPropertyAddress()).isEqualTo(propertyAddress);
                assertThat(p.getContactNumber()).isEqualTo(contactNumber);
                assertThat(p.getTenantId()).isEqualTo(tenantId);
                assertThat(p.getTenant()).isEqualTo(tenant);
                assertThat(p.getRooms())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(rooms);
                assertThat(p.getRoomTypes())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(roomTypes);
                assertThat(p.getRoomAvailabilities())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(roomAvailabilities);
                assertThat(p.getBookings())
                    .isNotNull()
                    .hasSize(1)
                    .isEqualTo(bookings);
            });
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Property property = new Property();

        // Then
        assertThat(property)
            .isNotNull()
            .satisfies(p -> {
                assertThat(p.getPropertyId()).isNull();
                assertThat(p.getPropertyName()).isNull();
                assertThat(p.getPropertyAddress()).isNull();
                assertThat(p.getContactNumber()).isNull();
                assertThat(p.getTenantId()).isNull();
                assertThat(p.getTenant()).isNull();
                assertThat(p.getRooms()).isNull();
                assertThat(p.getRoomTypes()).isNull();
                assertThat(p.getRoomAvailabilities()).isNull();
                assertThat(p.getBookings()).isNull();
            });
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Property property1 = Property.builder()
            .propertyId(1L)
            .propertyName("Grand Hotel")
            .propertyAddress("123 Main St")
            .tenantId(1L)
            .build();

        Property property2 = Property.builder()
            .propertyId(1L)
            .propertyName("Grand Hotel")
            .propertyAddress("123 Main St")
            .tenantId(1L)
            .build();

        Property property3 = Property.builder()
            .propertyId(2L)
            .propertyName("Luxury Resort")
            .propertyAddress("456 Beach Rd")
            .tenantId(2L)
            .build();

        // Then
        assertThat(property1)
            .isEqualTo(property2)    // Equal objects
            .isNotEqualTo(property3) // Different objects
            .isNotEqualTo(null)      // Null check
            .isNotEqualTo(new Object()); // Different type

        assertThat(property1.hashCode())
            .isEqualTo(property2.hashCode())
            .isNotEqualTo(property3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Property property = Property.builder()
            .propertyId(1L)
            .propertyName("Grand Hotel")
            .propertyAddress("123 Main St")
            .tenantId(1L)
            .build();

        // Then
        assertThat(property.toString())
            .isNotNull()
            .contains(
                "propertyId=1",
                "propertyName=Grand Hotel",
                "propertyAddress=123 Main St",
                "tenantId=1"
            );
    }

    @Test
    void testSetters() {
        // Given
        Property property = new Property();
        Long propertyId = 1L;
        String propertyName = "Grand Hotel";
        String propertyAddress = "123 Main St";
        String contactNumber = "+1234567890";
        Long tenantId = 1L;
        Tenant tenant = Tenant.builder().tenantId(tenantId).build();
        List<Room> rooms = Collections.singletonList(Room.builder().roomId(1L).build());
        List<RoomType> roomTypes = Collections.singletonList(RoomType.builder().roomTypeId(1L).build());
        List<RoomAvailability> roomAvailabilities = Collections.singletonList(RoomAvailability.builder().availabilityId(1L).build());
        List<Booking> bookings = Collections.singletonList(Booking.builder().bookingId(1L).build());

        // When
        property.setPropertyId(propertyId);
        property.setPropertyName(propertyName);
        property.setPropertyAddress(propertyAddress);
        property.setContactNumber(contactNumber);
        property.setTenantId(tenantId);
        property.setTenant(tenant);
        property.setRooms(rooms);
        property.setRoomTypes(roomTypes);
        property.setRoomAvailabilities(roomAvailabilities);
        property.setBookings(bookings);

        // Then
        assertThat(property)
            .satisfies(p -> {
                assertThat(p.getPropertyId()).isEqualTo(propertyId);
                assertThat(p.getPropertyName()).isEqualTo(propertyName);
                assertThat(p.getPropertyAddress()).isEqualTo(propertyAddress);
                assertThat(p.getContactNumber()).isEqualTo(contactNumber);
                assertThat(p.getTenantId()).isEqualTo(tenantId);
                assertThat(p.getTenant()).isEqualTo(tenant);
                assertThat(p.getRooms()).isEqualTo(rooms);
                assertThat(p.getRoomTypes()).isEqualTo(roomTypes);
                assertThat(p.getRoomAvailabilities()).isEqualTo(roomAvailabilities);
                assertThat(p.getBookings()).isEqualTo(bookings);
            });
    }
} 