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
              }
            }
        """;

    // Room Queries
    public static final String GET_ROOMS_BY_IDS = """
            query getRooms($availableRoomIds: [Int!]!) {
                listRooms(where: {
                    room_id: {in: $availableRoomIds}
                }) {
                    room_id
                    room_number
                    room_type {
                        room_type_id
                        room_type_name
                        max_capacity
                        room_rates {
                            room_rate {
                                room_rate_id
                                basic_nightly_rate
                                date
                            }
                        }
                    }
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
    public static final String GET_AVAILABLE_ROOMS = """
                query getAvailableRooms($propertyId: Int!, $startDate: AWSDateTime!, $endDate: AWSDateTime!) {
                    listRoomAvailabilities(
                        where: {
                            property: {
                                property_id: {equals: $propertyId}
                            },
                            date: {
                                gte: $startDate,
                                lte: $endDate
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
                        room_type_id
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
                    property_id
                }
            }
        """;
}