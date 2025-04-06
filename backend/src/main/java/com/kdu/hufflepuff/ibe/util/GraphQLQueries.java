package com.kdu.hufflepuff.ibe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class to store all GraphQL queries used in the application.
 * This centralizes query management and makes it easier to maintain and update
 * queries.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GraphQLQueries {
    // Property Queries
    public static final String GET_PROPERTIES_BY_TENANT = """
            query MyQuery ($tenantId: Int!) {
                listProperties(where: {tenant: {tenant_id: {equals: $tenantId}}}) {
                    property_name
                    property_id
                    property_address
                }
            }
        """;

    public static final String GET_PROPERTY_BY_ID = """
            query getProperty($propertyId: Int!, $tenantId: Int!) {
                getProperty(where: {
                    property_id: {equals: $propertyId},
                    tenant: {tenant_id: {equals: $tenantId}}
                }) {
                    property_id
                    property_name
                    property_address
                    contact_number
                    tenant_id
                }
            }
        """;

    public static final String GET_ROOM_RATE_MAPPINGS_BY_ROOM_TYPES = """
            query getRoomRateMappings($roomTypeIds: [Int!]!, $startDate: AWSDateTime!, $endDate: AWSDateTime!) {
                listRoomRateRoomTypeMappings(
                    where: {
                        room_type_id: {in: $roomTypeIds},
                        room_rate: {date: {gte: $startDate, lte: $endDate}}
                    }
                ) {
                    room_rate {
                        basic_nightly_rate
                        date
                    }
                    room_type {
                        room_type_id
                    }
                }
            }
        """;

    // Room Availability Queries
    public static final String GET_AVAILABLE_ROOMS_BY_PROPERTY_ID = """
            query getAvailableRooms($propertyId: Int!, $startDate: AWSDateTime!, $endDate: AWSDateTime!) {
                listRoomAvailabilities(
                    where: {
                        property: {
                            property_id: {equals: $propertyId}
                        },
                        date: {
                            gte: $startDate,
                            lt: $endDate
                        },
                        booking: {
                            booking_status: {
                                status: {not: {equals: "BOOKED"}}
                            }
                        }
                    }
                    take: 1000
                ) {
                availability_id
                date
                room {
                   room_id
                   room_number
                   room_type_id
                   room_type {
                     room_type_id
                   }
                }
            }
        }
        """;

    public static final String GET_AVAILABLE_ROOMS_BY_ROOM_TYPE_ID = """
            query getAvailableRooms($roomTypeId: Int!, $startDate: AWSDateTime!, $endDate: AWSDateTime!) {
                listRoomAvailabilities(
                    where: {
                        date: {
                            gte: $startDate,
                            lt: $endDate
                        },
                        booking: {
                            booking_status: {
                                status: {not: {equals: "BOOKED"}}
                            }
                        },
                        room: {
                            room_type_id: {
                                equals: $roomTypeId
                            }
                        }
                    }
                    take: 1000
                ) {
                availability_id
                date
                room {
                   room_id
                   room_number
                   room_type_id
                   room_type {
                     room_type_id
                   }
                }
                property {
                    property_id
                }
            }
        }
        """;

    // Room Type Queries
    public static final String GET_ROOM_TYPES_BY_PROPERTY = """
            query getRoomTypes($propertyId: Int!) {
                listRoomTypes(where: {
                    property_id: {equals: $propertyId}
                }) {
                    room_type_id
                    room_type_name
                    max_capacity
                    area_in_square_feet
                    single_bed
                    double_bed
                    property_of {
                        property_id
                        property_address
                    }
                }
            }
        """;

    public static final String GET_ALL_PROMOTIONS = """
            query getRoomRateMappings {
                listPromotions {
                    minimum_days_of_stay
                    price_factor
                    promotion_description
                    promotion_id
                    promotion_title
                    is_deactivated
                }
            }
        """;

    public static final String GET_BOOKING_DATA = """
           query GetBooking($bookingId: Int!){
                getBooking(where: {booking_id: $bookingId}) {
                    adult_count
                    amount_due_at_resort
                    booking_id
                    booking_status {
                        status
                    }
                    check_in_date
                    check_out_date
                    child_count
                    guest {
                        guest_name
                        guest_id
                    }
                    promotion_applied {
                        is_deactivated
                        minimum_days_of_stay
                        price_factor
                        promotion_description
                        promotion_id
                        promotion_title
                    }
                    property_booked {
                        property_name
                        property_id
                        property_address
                        contact_number
                    }
                    total_cost
                    room_booked {
                        date
                        room {
                            room_number
                            room_id
                        }
                    }
                }
            }
        """;

    public static final String GET_GUEST = """
            query GetGuest($guestId: Int!) {
               getGuest(where: {guest_id: $guestId}) {
                 guest_id
                 guest_name
               }
             }
        """;
}