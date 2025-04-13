package com.kdu.hufflepuff.ibe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphQLMutations {
    public static final String UPDATE_ROOM_AVAILABILITY = """
            mutation UpdateRoomAvailability(
                $availabilityId: Int!,
                $bookingId: Int!
            ) {
                updateRoomAvailability(
                    where: {
                        availability_id: $availabilityId
                    }
                    data: {
                        booking: {connect: {booking_id: $bookingId}}
                    }
                ) {
                    availability_id
                    booking_id
                    property_id
                    room_id
                    date
                }
            }
        """;

    public static final String CREATE_GUEST = """
            mutation CreateGuest($guestName: String!) {
                createGuest(
                    data: {
                        guest_name: $guestName,
                    }
                ) {
                    guest_id
                    guest_name
                }
            }
        """;

    public static final String CREATE_GUEST_WITH_ID = """
            mutation CreateGuest($guestId: Int!, $guestName: String!) {
                createGuest(
                    data: {
                        guest_id: $guestId
                        guest_name: $guestName,
                    }
                ) {
                    guest_id
                    guest_name
                }
            }
        """;

    public static final String UPDATE_BOOKING_STATUS = """
            mutation UpdateBookingStatus(
                $bookingId: Int!,
                $statusId: Int!
            ) {
                updateBooking(
                    where: {
                        booking_id: $bookingId
                    }
                    data: {
                        booking_status: {connect: {status_id: $statusId}}
                    }
                ) {
                    booking_id
                    status_id
                }
            }
        """;

    public static String getCreateBookingQuery(boolean includePromotion) {
        String baseQuery = """
                mutation CreateBooking(
                    $checkInDate: AWSDateTime!,
                    $checkOutDate: AWSDateTime!,
                    $adultCount: Int!,
                    $childCount: Int!,
                    $totalCost: Int!,
                    $amountDueAtResort: Int!,
                    $propertyId: Int!,
                    $guestId: Int!%s
                ) {
                    createBooking(
                        data: {
                            check_in_date: $checkInDate,
                            check_out_date: $checkOutDate,
                            adult_count: $adultCount,
                            child_count: $childCount,
                            total_cost: $totalCost,
                            amount_due_at_resort: $amountDueAtResort,
                            property_booked: { connect: { property_id: $propertyId } },
                            booking_status: { connect: { status_id: 1 } },
                            guest: { connect: { guest_id: $guestId } }%s
                        }
                    ) {
                        booking_id
                        check_in_date
                        check_out_date
                        adult_count
                        child_count
                        total_cost
                        amount_due_at_resort
                        guest {
                            guest_id
                            guest_name
                        }
                        promotion_applied {
                            promotion_id
                            price_factor
                            promotion_title
                            promotion_description
                            minimum_days_of_stay
                            is_deactivated
                        }
                    }
                }
            """;

        if (includePromotion) {
            return String.format(baseQuery,
                ",\n                $promotionId: Int!",
                ",\n                        promotion_applied: { connect: { promotion_id: $promotionId } }");
        } else {
            return String.format(baseQuery, "", "");
        }
    }
}
