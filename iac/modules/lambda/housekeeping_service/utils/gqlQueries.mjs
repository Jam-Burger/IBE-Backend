const AVAILABILITY_BODY = `
    availability_id
    date
    room {
        room_id
        room_number
    }
`;

export const GET_BOOKED_ROOMS_ON_DATE = `
    query GetBookedRooms($propertyId: Int!, $date: AWSDateTime!) {
        listRoomAvailabilities(
            where: {property: {property_id: {equals: $propertyId}}, booking: {booking_status: {status: {equals: "BOOKED"}}}, date: {equals: $date}}
        ) {
            ${AVAILABILITY_BODY}
        }
    }
`;

export const GET_CHECKING_IN_ROOMS = `
    query GetBookedRooms($propertyId: Int!, $date: AWSDateTime!) {
        listRoomAvailabilities(
            where: {property: {property_id: {equals: $propertyId}}, booking: {booking_status: {status: {equals: "BOOKED"}}, check_in_date: {equals: $date}}}
        ) {
            ${AVAILABILITY_BODY}
        }
    }
`;

export const GET_CHECKING_OUT_ROOMS = `
    query GetBookedRooms($propertyId: Int!, $date: AWSDateTime!) {
        listRoomAvailabilities(
            where: {property: {property_id: {equals: $propertyId}}, booking: {booking_status: {status: {equals: "BOOKED"}}, check_out_date: {equals: $date}}}
        ) {
            ${AVAILABILITY_BODY}
        }
    }
`;

export const GET_PROPERTY_DETAILS = `
    query getProperty($propertyId: Int!) {
        getProperty(where: {
            property_id: $propertyId
        }) {
            property_id
            property_name
            property_address
            contact_number
            tenant_id
        }
    }`;