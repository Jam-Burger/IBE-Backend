// Endpoint configurations with method, body, and query parameters
export const ENDPOINT_CONFIGS = {
  // Health endpoints
  HEALTH: {
    path: '/health',
    method: 'GET',
    body: null,
    queryParams: null,
  },

  // Authentication endpoints
  AUTH: {
    LOGIN: {
      path: '/api/v1/1/auth/login',
      method: 'POST',
      body: {
        email: 'themirza001@gmail.com',
        password: 'password',
      },
      queryParams: null,
    },
  },

  // Property endpoints
  PROPERTY: {
    DETAIL: {
      path: '/api/v1/properties/9',
      method: 'GET',
      body: null,
      queryParams: null,
    },
  },

  // Room Type endpoints
  ROOM_TYPE: {
    LIST: {
      path: '/api/v1/room-types',
      method: 'GET',
      body: null,
      queryParams: {
        page: 0,
        size: 10,
      },
    },
    DETAIL: {
      path: '/api/v1/room-types/{id}',
      method: 'GET',
      body: null,
      queryParams: null,
    },
  },

  // Booking endpoints
  BOOKING: {
    LIST: {
      path: '/api/v1/bookings',
      method: 'GET',
      body: null,
      queryParams: {
        page: 0,
        size: 10,
      },
    },
    CREATE: {
      path: '/api/v1/bookings',
      method: 'POST',
      body: {
        roomTypeId: 1,
        checkInDate: '2024-04-25',
        checkOutDate: '2024-04-26',
        guestCount: 2,
      },
      queryParams: null,
    },
  },

  // Special Offer endpoints
  SPECIAL_OFFER: {
    LIST: {
      path: '/api/v1/special-offers',
      method: 'GET',
      body: null,
      queryParams: {
        page: 0,
        size: 10,
      },
    },
    CREATE: {
      path: '/api/v1/special-offers',
      method: 'POST',
      body: {
        name: 'Test Offer',
        description: 'Test Description',
        discountPercentage: 10,
        startDate: '2024-04-25',
        endDate: '2024-04-26',
      },
      queryParams: null,
    },
  },
};

// Test endpoint groups
export const TEST_ENDPOINTS = {
  SMOKE: [
    ENDPOINT_CONFIGS.HEALTH,
    // ENDPOINT_CONFIGS.PROPERTY.LIST,
    // ENDPOINT_CONFIGS.ROOM_TYPE.LIST,
  ],
  MAIN: [
    ENDPOINT_CONFIGS.HEALTH,
    // ENDPOINT_CONFIGS.PROPERTY.LIST,
    // ENDPOINT_CONFIGS.ROOM_TYPE.LIST,
    // ENDPOINT_CONFIGS.BOOKING.LIST,
    // ENDPOINT_CONFIGS.SPECIAL_OFFER.LIST,
  ],
  STRESS: [
    ENDPOINT_CONFIGS.HEALTH,
    // ENDPOINT_CONFIGS.AUTH.LOGIN,
    // ENDPOINT_CONFIGS.PROPERTY.LIST,
    // ENDPOINT_CONFIGS.ROOM_TYPE.LIST
  ],
}; 