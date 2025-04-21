// Base configuration
export const BASE_CONFIG = {
    BASE_URL: __ENV.BASE_URL || 'https://ala2vbnbel.execute-api.ap-south-1.amazonaws.com/dev',
    DEFAULT_DELAY: 1, // seconds
};

// Main test configuration
export const MAIN_CONFIG = {
    STAGES: [
        {duration: '1m', target: 50},    // Ramp up to 50 users
        {duration: '3m', target: 50},    // Stay at 50 users
        {duration: '1m', target: 0},     // Ramp down
    ],
    THRESHOLDS: {
        'http_req_duration': ['p(95)<500'],
        'http_req_failed': ['rate<0.1'],
    },
    DELAY: 1, // seconds
};

// Stress test configuration
export const STRESS_CONFIG = {
    STAGES: [
        {duration: '2m', target: 1000},   // Ramp up to 1000 users
        {duration: '3m', target: 1000},   // Stay at 1000 users
        {duration: '2m', target: 2000},   // Increase to 2000 users
        {duration: '3m', target: 2000},   // Stay at 2000 users
        {duration: '2m', target: 3000},   // Increase to 3000 users
        {duration: '3m', target: 3000},   // Stay at 3000 users
        {duration: '2m', target: 0},     // Ramp down
    ],
    THRESHOLDS: {
        'http_req_duration': ['p(95)<1000'],  // 1 second max for 95th percentile
        'http_req_failed': ['rate<0.1'],      // Less than 10% errors
    },
    DELAY: 0.5, // seconds between requests
};

// HTML Report configuration
export const REPORT_CONFIG = {
    OUTPUT_DIR: 'reports',
    getFilename: (testType) => {
        return `${testType}-report-[${BASE_CONFIG.BASE_URL.split("//")[1]}].html`;
    }
};