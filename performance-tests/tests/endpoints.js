// Endpoint configurations with method, body, and query parameters
export const ENDPOINT_CONFIGS = {
    // Health endpoints
    HEALTH: {
        path: '/health',
        method: 'GET',
        body: null,
        queryParams: null,
    },

    //Config endpoints
    CONFIG: {
        GLOBAL: {
            path: "/api/v1/1/config/GLOBAL",
            method: "GET",
            body: null,
            queryParams: null,
        },
        LANDING: {
            path: "/api/v1/1/config/LANDING",
            method: "GET",
            body: null,
            queryParams: null,
        },
        CHECKOUT: {
            path: "/api/v1/1/config/CHECKOUT",
            method: "GET",
            body: null,
            queryParams: null,
        },
        ROOM_LIST: {
            path: "/api/v1/1/config/ROOMS_LIST",
            method: "GET",
            body: null,
            queryParams: null,
        },
    },

    // Property endpoints
    PROPERTY: {
        DETAIL: {
            path: '/api/v1/1/properties/9',
            method: 'GET',
            body: null,
            queryParams: null,
        },
    },

    //Room endpoints
    ROOM: {
        RATES: {
            path: '/api/v1/1/9/room-rates/daily-minimum',
            method: 'GET',
            body: null,
            queryParams: {
                start_date: '2025-05-01',
                end_date: '2025-06-30',
            }
        },
        AMENITIES: {
            path: '/api/v1/1/9/amenities',
            method: 'GET',
            body: null,
            queryParams: null
        }
    },

    //Offer endpoints
    OFFERS: {
        SPECIAL_DISCOUNT: {
            path: "/api/v1/1/9/special-discounts",
            method: "GET",
            body: null,
            queryParams: {
                start_date: '2025-05-01',
                end_date: '2025-06-30',
            }
        },
        CALENDAR_OFFERS: {
            path: "/api/v1/1/9/special-discounts/calender-offers",
            method: "GET",
            body: null,
            queryParams: {
                start_date: '2025-05-01',
                end_date: '2025-06-30',
            }
        },
        PROMO_OFFERS: {
            path: "/api/v1/1/9/special-discounts/promo-offer",
            method: "GET",
            body: null,
            queryParams: {
                start_date: '2025-06-01',
                end_date: '2025-06-15',
                promo_code: 'SUMMER33'
            }
        },
    },

    // Room Type endpoints
    ROOM_TYPE: {
        LIST: {
            path: '/api/v1/1/9/room-types/filter',
            method: 'GET',
            body: null,
            queryParams: {
                propertyId: 9,
                roomCount: 1,
                totalGuests: 2,
                guest_Adults: 2,
                sortBy: "CAPACITY_HIGH_TO_LOW",
                dateFrom: "2025-04-05",
                dateTo: "2025-04-10",
                roomSizeMin: 1,
                roomSizeMax: 750,
                pageSize: 3,
                page: 1
            },
        },
        DETAIL: {
            path: '/api/v1/1/1/room-types/1',
            method: 'GET',
            body: null,
            queryParams: {
                dateFrom: '2025-05-01',
                dateTo: '2025-05-14',
            },
        },
    },

    // Booking endpoints
    BOOKING: {
        LIST: {
            path: '/api/v1/1/bookings/466',
            method: 'GET',
            body: null,
            queryParams: null,
        },
    },
}

// Test endpoint groups
export const TEST_ENDPOINTS = [
    ENDPOINT_CONFIGS.HEALTH,
    ENDPOINT_CONFIGS.CONFIG.GLOBAL,
    ENDPOINT_CONFIGS.CONFIG.LANDING,
    ENDPOINT_CONFIGS.CONFIG.CHECKOUT,
    ENDPOINT_CONFIGS.CONFIG.ROOM_LIST,
    ENDPOINT_CONFIGS.PROPERTY.DETAIL,
    ENDPOINT_CONFIGS.ROOM.RATES,
    ENDPOINT_CONFIGS.ROOM.AMENITIES,
    ENDPOINT_CONFIGS.ROOM_TYPE.LIST,
    ENDPOINT_CONFIGS.ROOM_TYPE.DETAIL,
    ENDPOINT_CONFIGS.OFFERS.SPECIAL_DISCOUNT,
    ENDPOINT_CONFIGS.OFFERS.CALENDAR_OFFERS,
    ENDPOINT_CONFIGS.OFFERS.PROMO_OFFERS,
    ENDPOINT_CONFIGS.BOOKING.LIST
];