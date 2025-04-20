import {GraphQLClient} from "graphql-request";
import {
    GET_BOOKED_ROOMS_ON_DATE,
    GET_CHECKING_IN_ROOMS,
    GET_CHECKING_OUT_ROOMS, GET_PROPERTY_DETAILS
} from "../utils/gqlQueries.mjs";

const endpoint = process.env.GRAPHQL_API_URL;
const apiKey = process.env.GRAPHQL_API_KEY;

const client = new GraphQLClient(endpoint, {
    headers: {
        'x-api-key': apiKey,
        'Content-Type': 'application/json',
    },
});

export async function getBookedRooms(propertyId, date) {
    try {
        const data = await client.request(GET_BOOKED_ROOMS_ON_DATE, {
            propertyId,
            date
        });
        return convertToSet(data.listRoomAvailabilities);
    } catch (error) {
        console.error('Error fetching booked rooms:', error);
        throw error;
    }
}

export async function getCheckingInRooms(propertyId, date) {
    try {
        const data = await client.request(GET_CHECKING_IN_ROOMS, {
            propertyId,
            date
        });
        return convertToSet(data.listRoomAvailabilities);
    } catch (error) {
        console.error('Error fetching checking-in rooms:', error);
        throw error;
    }
}

export async function getCheckingOutRooms(propertyId, date) {
    try {
        const data = await client.request(GET_CHECKING_OUT_ROOMS, {
            propertyId,
            date
        });
        return convertToSet(data.listRoomAvailabilities);
    } catch (error) {
        console.error('Error fetching checking-out rooms:', error);
        throw error;
    }
}

export async function getPropertyDetails(propertyId) {
    try{
        const data= await client.request(GET_PROPERTY_DETAILS, {
            propertyId
        });
        return data.getProperty;
    } catch (error) {
        console.error('Error fetching property details:', error);
        throw error;
    }
}

function convertToSet(availabilities) {
    const roomSet = new Set();
    availabilities.forEach(availability => {
        roomSet.add(availability.room.room_id);
    });
    return roomSet;
}